package com.davexo.backend.data;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.davexo.backend.entity.Budget;
import com.davexo.backend.entity.Expense;
import com.davexo.backend.entity.ExpenseCategory;
import com.davexo.backend.entity.InventoryCategory;
import com.davexo.backend.entity.InventoryItem;
import com.davexo.backend.entity.Task;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.BudgetScope;
import com.davexo.backend.enums.InventoryItemType;
import com.davexo.backend.enums.InventoryStatus;
import com.davexo.backend.enums.Role;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.repository.BudgetRepository;
import com.davexo.backend.repository.ExpenseCategoryRepository;
import com.davexo.backend.repository.ExpenseRepository;
import com.davexo.backend.repository.InventoryCategoryRepository;
import com.davexo.backend.repository.InventoryItemRepository;
import com.davexo.backend.repository.TaskRepository;
import com.davexo.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer implements ApplicationRunner {

    private static final String DEV_EMAIL = "dev@davexo.local";
    private static final String DEV_PASSWORD = "Dev1234!";

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final TaskRepository taskRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmailIgnoreCase(DEV_EMAIL)) {
            return;
        }

        User user = createUser();

        createBudget(
            "Budget mensuel global",
            "1800.00",
            BudgetScope.GLOBAL,
            user);

        Budget foodBudget = createBudget(
                "Budget alimentation",
                "450.00",
                BudgetScope.SELECTED_CATEGORIES,
                user);

        Budget transportBudget = createBudget(
                "Budget transport",
                "200.00",
                BudgetScope.SELECTED_CATEGORIES,
                user);

        ExpenseCategories expenseCategories = createExpenseCategories(user, foodBudget, transportBudget);

        createExpenses(user, expenseCategories);

        createTasks(user);

        createInventory(user);

        System.out.println("----------------------------------------");
        System.out.println("Davexo development data initialized");
        System.out.println("Email: " + DEV_EMAIL);
        System.out.println("Password: " + DEV_PASSWORD);
        System.out.println("----------------------------------------");
    }

    private User createUser() {

        User user = User.builder()
                .pseudo("Davexo Dev")
                .email(DEV_EMAIL)
                .passwordHash(passwordEncoder.encode(DEV_PASSWORD))
                .role(Role.USER)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    private Budget createBudget(
            String name,
            String amount,
            BudgetScope scope,
            User user) {

        Budget budget = Budget.builder()
                .name(name)
                .amount(new BigDecimal(amount))
                .scope(scope)
                .user(user)
                .build();

        return budgetRepository.save(budget);
    }

    private ExpenseCategories createExpenseCategories(
            User user,
            Budget foodBudget,
            Budget transportBudget) {

        ExpenseCategory groceries = createExpenseCategory(
                "Courses alimentaires",
                user,
                foodBudget);

        ExpenseCategory restaurants = createExpenseCategory(
                "Restaurants",
                user,
                foodBudget);

        ExpenseCategory transport = createExpenseCategory(
                "Transport",
                user,
                transportBudget);

        ExpenseCategory housing = createExpenseCategory(
                "Logement",
                user,
                null);

        ExpenseCategory leisure = createExpenseCategory(
                "Loisirs",
                user,
                null);

        ExpenseCategory health = createExpenseCategory(
                "Santé",
                user,
                null);

        ExpenseCategory subscriptions = createExpenseCategory(
                "Abonnements",
                user,
                null);

        ExpenseCategory miscellaneous = createExpenseCategory(
                "Divers",
                user,
                null);

        return new ExpenseCategories(
                groceries,
                restaurants,
                transport,
                housing,
                leisure,
                health,
                subscriptions,
                miscellaneous);
    }

    private ExpenseCategory createExpenseCategory(
            String name,
            User user,
            Budget budget) {

        ExpenseCategory category = ExpenseCategory.builder()
                .name(name)
                .user(user)
                .budget(budget)
                .build();

        return expenseCategoryRepository.save(category);
    }

    private void createExpenses(
            User user,
            ExpenseCategories categories) {

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth twoMonthsAgo = currentMonth.minusMonths(2);
        YearMonth fourMonthsAgo = currentMonth.minusMonths(4);
        YearMonth fiveMonthsAgo = currentMonth.minusMonths(5);
        YearMonth sixMonthsAgo = currentMonth.minusMonths(6);

        /*
         * Le mois currentMonth - 3 est volontairement vide.
         *
         * Cela permet de tester la moyenne mensuelle historique :
         * le mois doit compter dans la moyenne même s'il contient 0 €.
         */

        List<Expense> expenses = List.of(

                // Six mois auparavant
                expense(
                        "Loyer",
                        "720.00",
                        "Loyer mensuel",
                        sixMonthsAgo.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses semaine",
                        "74.35",
                        null,
                        sixMonthsAgo.atDay(5),
                        categories.groceries(),
                        user),
                expense(
                        "Essence",
                        "58.40",
                        null,
                        sixMonthsAgo.atDay(12),
                        categories.transport(),
                        user),

                // Cinq mois auparavant
                expense(
                        "Loyer",
                        "720.00",
                        null,
                        fiveMonthsAgo.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses alimentaires",
                        "92.70",
                        null,
                        fiveMonthsAgo.atDay(8),
                        categories.groceries(),
                        user),
                expense(
                        "Cinéma",
                        "14.50",
                        null,
                        fiveMonthsAgo.atDay(15),
                        categories.leisure(),
                        user),
                expense(
                        "Pharmacie",
                        "26.80",
                        null,
                        fiveMonthsAgo.atDay(22),
                        categories.health(),
                        user),

                // Quatre mois auparavant
                expense(
                        "Loyer",
                        "720.00",
                        null,
                        fourMonthsAgo.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses",
                        "110.25",
                        null,
                        fourMonthsAgo.atDay(10),
                        categories.groceries(),
                        user),
                expense(
                        "Restaurant",
                        "42.90",
                        "Repas entre amis",
                        fourMonthsAgo.atDay(18),
                        categories.restaurants(),
                        user),
                expense(
                        "Streaming",
                        "15.99",
                        null,
                        fourMonthsAgo.atEndOfMonth(),
                        categories.subscriptions(),
                        user),

                /*
                 * currentMonth - 3 :
                 * aucune dépense volontairement.
                 */

                // Deux mois auparavant
                expense(
                        "Loyer",
                        "720.00",
                        null,
                        twoMonthsAgo.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses",
                        "83.45",
                        null,
                        twoMonthsAgo.atDay(4),
                        categories.groceries(),
                        user),
                expense(
                        "Train",
                        "46.00",
                        null,
                        twoMonthsAgo.atDay(9),
                        categories.transport(),
                        user),
                expense(
                        "Restaurant",
                        "35.90",
                        null,
                        twoMonthsAgo.atDay(17),
                        categories.restaurants(),
                        user),
                expense(
                        "Jeu vidéo",
                        "39.99",
                        null,
                        twoMonthsAgo.atEndOfMonth(),
                        categories.leisure(),
                        user),

                // Mois précédent
                expense(
                        "Loyer",
                        "720.00",
                        null,
                        previousMonth.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses début de mois",
                        "86.20",
                        null,
                        previousMonth.atDay(3),
                        categories.groceries(),
                        user),
                expense(
                        "Essence",
                        "62.50",
                        null,
                        previousMonth.atDay(7),
                        categories.transport(),
                        user),
                expense(
                        "Restaurant",
                        "54.30",
                        null,
                        previousMonth.atDay(12),
                        categories.restaurants(),
                        user),
                expense(
                        "Spotify",
                        "11.12",
                        null,
                        previousMonth.atDay(15),
                        categories.subscriptions(),
                        user),
                expense(
                        "Médecin",
                        "30.00",
                        null,
                        previousMonth.atDay(18),
                        categories.health(),
                        user),
                expense(
                        "Achat imprévu",
                        "28.90",
                        null,
                        previousMonth.atEndOfMonth(),
                        categories.miscellaneous(),
                        user),

                // Mois courant
                expense(
                        "Loyer",
                        "720.00",
                        null,
                        currentMonth.atDay(1),
                        categories.housing(),
                        user),
                expense(
                        "Courses",
                        "68.75",
                        null,
                        currentMonth.atDay(
                                Math.min(3, LocalDate.now().getDayOfMonth())),
                        categories.groceries(),
                        user),
                expense(
                        "Bus",
                        "24.50",
                        null,
                        currentMonth.atDay(
                                Math.min(6, LocalDate.now().getDayOfMonth())),
                        categories.transport(),
                        user),
                expense(
                        "Restaurant",
                        "31.80",
                        null,
                        currentMonth.atDay(
                                Math.min(10, LocalDate.now().getDayOfMonth())),
                        categories.restaurants(),
                        user),
                expense(
                        "Netflix",
                        "13.49",
                        null,
                        currentMonth.atDay(
                                Math.min(15, LocalDate.now().getDayOfMonth())),
                        categories.subscriptions(),
                        user),
                expense(
                        "Courses complémentaires",
                        "45.60",
                        null,
                        LocalDate.now(),
                        categories.groceries(),
                        user));

        expenseRepository.saveAll(expenses);
    }

    private Expense expense(
            String label,
            String amount,
            String note,
            LocalDate expenseDate,
            ExpenseCategory category,
            User user) {

        return Expense.builder()
                .label(label)
                .amount(new BigDecimal(amount))
                .note(note)
                .expenseDate(expenseDate)
                .expenseCategory(category)
                .user(user)
                .build();
    }

    private void createTasks(User user) {

        LocalDate today = LocalDate.now();

        LocalDate currentWeekMonday = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate previousWeekMonday = currentWeekMonday.minusWeeks(1);

        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        YearMonth twoMonthsAgo = YearMonth.now().minusMonths(2);

        List<Task> tasks = List.of(

                // Anciennes tâches terminées
                task(
                        "Renouveler assurance",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        twoMonthsAgo.atDay(2),
                        twoMonthsAgo.atDay(12),
                        twoMonthsAgo.atDay(8),
                        "Terminée avant échéance",
                        user),

                task(
                        "Prendre rendez-vous médical",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        twoMonthsAgo.atDay(4),
                        twoMonthsAgo.atDay(10),
                        twoMonthsAgo.atDay(10),
                        "Terminée exactement le jour prévu",
                        user),

                task(
                        "Ranger les documents administratifs",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        twoMonthsAgo.atDay(1),
                        twoMonthsAgo.atDay(8),
                        twoMonthsAgo.atDay(15),
                        "Terminée en retard",
                        user),

                // Mois précédent
                task(
                        "Déclarer un changement administratif",
                        TaskPriority.URGENT,
                        TaskStatus.COMPLETED,
                        previousMonth.atDay(1),
                        previousMonth.atDay(5),
                        previousMonth.atDay(3),
                        null,
                        user),

                task(
                        "Réviser le budget mensuel",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        previousMonth.atDay(4),
                        previousMonth.atDay(10),
                        previousMonth.atDay(12),
                        "Terminée deux jours en retard",
                        user),

                task(
                        "Nettoyer le garage",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        previousMonth.atDay(2),
                        null,
                        previousMonth.atDay(20),
                        "Tâche sans échéance",
                        user),

                task(
                        "Commander une pièce de rechange",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        previousMonth.atDay(15),
                        previousMonth.atEndOfMonth(),
                        null,
                        null,
                        user),

                // Semaine précédente
                task(
                        "Envoyer un document important",
                        TaskPriority.URGENT,
                        TaskStatus.COMPLETED,
                        previousWeekMonday.minusDays(2),
                        previousWeekMonday.plusDays(1),
                        previousWeekMonday.plusDays(1),
                        "Terminée exactement à l'échéance",
                        user),

                task(
                        "Faire les courses",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        previousWeekMonday,
                        previousWeekMonday.plusDays(4),
                        previousWeekMonday.plusDays(2),
                        null,
                        user),

                task(
                        "Réorganiser une étagère",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        previousWeekMonday,
                        previousWeekMonday.plusDays(3),
                        previousWeekMonday.plusDays(5),
                        "Terminée après la date prévue",
                        user),

                // Semaine actuelle
                task(
                        "Finaliser une fonctionnalité importante",
                        TaskPriority.URGENT,
                        TaskStatus.COMPLETED,
                        currentWeekMonday.minusDays(3),
                        currentWeekMonday,
                        currentWeekMonday,
                        null,
                        user),

                task(
                        "Mettre à jour la documentation",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        currentWeekMonday.minusDays(7),
                        currentWeekMonday.plusDays(1),
                        dateNotAfterToday(currentWeekMonday.plusDays(1)),
                        null,
                        user),

                task(
                        "Corriger une anomalie",
                        TaskPriority.URGENT,
                        TaskStatus.COMPLETED,
                        currentWeekMonday.minusDays(4),
                        dateNotAfterToday(currentWeekMonday.plusDays(2)),
                        dateNotAfterToday(currentWeekMonday.plusDays(3)),
                        "Cas potentiellement terminé en retard",
                        user),

                task(
                        "Lire une documentation technique",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.TO_DO,
                        currentWeekMonday,
                        today.plusDays(3),
                        null,
                        null,
                        user),

                task(
                        "Préparer les prochaines tâches",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        today,
                        today.plusDays(5),
                        null,
                        null,
                        user),

                // Pas de dueDate mais terminée aujourd'hui
                task(
                        "Classer les téléchargements",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        today.minusDays(12),
                        null,
                        today,
                        "Teste les statistiques basées sur completedAt sans dueDate",
                        user),

                // Créée et terminée le même jour
                task(
                        "Répondre à un message",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        today,
                        today,
                        today,
                        "Durée de traitement : 0 jour",
                        user),

                // Tâche longue
                task(
                        "Travail de fond",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        today.minusDays(30),
                        today.plusDays(2),
                        today,
                        "Teste une durée de réalisation longue",
                        user),

                // Sans échéance et non terminée
                task(
                        "Apprendre un nouveau sujet",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.TO_DO,
                        today.minusDays(5),
                        null,
                        null,
                        "Pas de date limite",
                        user));

        taskRepository.saveAll(tasks);
    }

    private Task task(
            String title,
            TaskPriority priority,
            TaskStatus status,
            LocalDate createdAt,
            LocalDate dueDate,
            LocalDate completedAt,
            String note,
            User user) {

        return Task.builder()
                .title(title)
                .priority(priority)
                .status(status)
                .createdAt(createdAt)
                .dueDate(dueDate)
                .completedAt(completedAt)
                .note(note)
                .user(user)
                .build();
    }

    private LocalDate dateNotAfterToday(LocalDate date) {
        return date.isAfter(LocalDate.now())
                ? LocalDate.now()
                : date;
    }

    private void createInventory(User user) {

        InventoryCategory food = createInventoryCategory(
                "Alimentation",
                user);

        InventoryCategory hygiene = createInventoryCategory(
                "Hygiène",
                user);

        InventoryCategory cleaning = createInventoryCategory(
                "Produits ménagers",
                user);

        InventoryCategory electronics = createInventoryCategory(
                "Électronique",
                user);

        InventoryCategory tools = createInventoryCategory(
                "Outillage",
                user);

        List<InventoryItem> items = List.of(

                inventoryItem(
                        "Riz",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.IN_STOCK,
                        null,
                        food,
                        user),

                inventoryItem(
                        "Pâtes",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.LOW_STOCK,
                        "Il reste environ un paquet",
                        food,
                        user),

                inventoryItem(
                        "Café",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.MISSING,
                        "À ajouter à la prochaine liste de courses",
                        food,
                        user),

                inventoryItem(
                        "Yaourts",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.MISSING,
                        null,
                        food,
                        user),

                inventoryItem(
                        "Dentifrice",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.LOW_STOCK,
                        null,
                        hygiene,
                        user),

                inventoryItem(
                        "Gel douche",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.MISSING,
                        null,
                        hygiene,
                        user),

                inventoryItem(
                        "Liquide vaisselle",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.IN_STOCK,
                        null,
                        cleaning,
                        user),

                inventoryItem(
                        "Lessive",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.NEED_MORE,
                        "Prévoir un second bidon",
                        cleaning,
                        user),

                inventoryItem(
                        "Aspirateur",
                        InventoryItemType.DURABLE,
                        InventoryStatus.IN_SERVICE,
                        null,
                        cleaning,
                        user),

                inventoryItem(
                        "Ancien ordinateur portable",
                        InventoryItemType.DURABLE,
                        InventoryStatus.OUT_OF_SERVICE,
                        "Ne démarre plus",
                        electronics,
                        user),

                inventoryItem(
                        "Casque audio",
                        InventoryItemType.DURABLE,
                        InventoryStatus.TO_REPLACE,
                        "Faux contact sur le câble",
                        electronics,
                        user),

                inventoryItem(
                        "Multiprise",
                        InventoryItemType.DURABLE,
                        InventoryStatus.NEED_MORE,
                        "Une supplémentaire serait utile",
                        electronics,
                        user),

                inventoryItem(
                        "Perceuse",
                        InventoryItemType.DURABLE,
                        InventoryStatus.IN_SERVICE,
                        null,
                        tools,
                        user),

                inventoryItem(
                        "Tournevis cruciforme",
                        InventoryItemType.DURABLE,
                        InventoryStatus.IN_SERVICE,
                        null,
                        tools,
                        user));

        inventoryItemRepository.saveAll(items);
    }

    private InventoryCategory createInventoryCategory(
            String name,
            User user) {

        InventoryCategory category = InventoryCategory.builder()
                .name(name)
                .user(user)
                .build();

        return inventoryCategoryRepository.save(category);
    }

    private InventoryItem inventoryItem(
            String name,
            InventoryItemType type,
            InventoryStatus status,
            String note,
            InventoryCategory category,
            User user) {

        return InventoryItem.builder()
                .name(name)
                .type(type)
                .status(status)
                .note(note)
                .inventoryCategory(category)
                .user(user)
                .build();
    }

    private record ExpenseCategories(
            ExpenseCategory groceries,
            ExpenseCategory restaurants,
            ExpenseCategory transport,
            ExpenseCategory housing,
            ExpenseCategory leisure,
            ExpenseCategory health,
            ExpenseCategory subscriptions,
            ExpenseCategory miscellaneous) {
    }
}