package com.davexo.backend.data;

import java.math.BigDecimal;
import java.time.Clock;
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

    private static final String SECONDARY_EMAIL = "secondary@davexo.local";
    private static final String SECONDARY_PASSWORD = "Secondary1234!";

    private static final String ADMIN_EMAIL = "admin@davexo.local";
    private static final String ADMIN_PASSWORD = "Admin1234!";

    private static final String DISABLED_EMAIL = "disabled@davexo.local";
    private static final String DISABLED_PASSWORD = "Disabled1234!";

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseRepository expenseRepository;
    private final TaskRepository taskRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmailIgnoreCase(DEV_EMAIL)) {
            return;
        }

        LocalDate today = LocalDate.now(clock);

        User mainUser = createUser(
                "Davexo Dev",
                DEV_EMAIL,
                DEV_PASSWORD,
                Role.USER,
                true,
                today.minusMonths(8));

        User secondaryUser = createUser(
                "Davexo Secondary",
                SECONDARY_EMAIL,
                SECONDARY_PASSWORD,
                Role.USER,
                true,
                today.minusMonths(2));

        createUser(
                "Davexo Admin",
                ADMIN_EMAIL,
                ADMIN_PASSWORD,
                Role.ADMIN,
                true,
                today.minusYears(1));

        createUser(
                "Davexo Disabled",
                DISABLED_EMAIL,
                DISABLED_PASSWORD,
                Role.USER,
                false,
                today.minusMonths(1));

        createMainUserData(mainUser, today);
        createSecondaryUserData(secondaryUser, today);

        printCredentials();
    }

    private User createUser(
            String pseudo,
            String email,
            String password,
            Role role,
            boolean active,
            LocalDate createdAt) {

        User user = User.builder()
                .pseudo(pseudo)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(createdAt)
                .role(role)
                .isActive(active)
                .build();

        return userRepository.save(user);
    }

    private void createMainUserData(User user, LocalDate today) {

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

        ExpenseCategories categories = createMainExpenseCategories(
                user,
                foodBudget,
                transportBudget);

        createMainExpenses(user, categories, today);
        createMainTasks(user, today);
        createMainInventory(user);
    }

    private void createSecondaryUserData(User user, LocalDate today) {

        Budget dailyLifeBudget = createBudget(
                "Budget vie quotidienne",
                "300.00",
                BudgetScope.SELECTED_CATEGORIES,
                user);

        ExpenseCategory groceries = createExpenseCategory(
                "Courses",
                user,
                dailyLifeBudget);

        ExpenseCategory housing = createExpenseCategory(
                "Logement",
                user,
                null);

        ExpenseCategory leisure = createExpenseCategory(
                "Loisirs",
                user,
                null);

        YearMonth currentMonth = YearMonth.from(today);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        expenseRepository.saveAll(List.of(
                expense(
                        "Loyer secondaire",
                        "620.00",
                        null,
                        previousMonth.atDay(1),
                        housing,
                        user),
                expense(
                        "Courses secondaires",
                        "72.40",
                        null,
                        previousMonth.atDay(8),
                        groceries,
                        user),
                expense(
                        "Cinéma",
                        "13.50",
                        null,
                        previousMonth.atDay(18),
                        leisure,
                        user),
                expense(
                        "Loyer secondaire",
                        "620.00",
                        null,
                        currentMonth.atDay(1),
                        housing,
                        user),
                expense(
                        "Courses secondaires",
                        "58.90",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 6),
                        groceries,
                        user)));

        taskRepository.saveAll(List.of(
                task(
                        "Envoyer un justificatif",
                        TaskPriority.URGENT,
                        TaskStatus.TO_DO,
                        today.minusDays(2),
                        today.plusDays(1),
                        null,
                        null,
                        user),
                task(
                        "Préparer la semaine",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        today.minusDays(1),
                        today.plusDays(4),
                        null,
                        null,
                        user),
                task(
                        "Trier les photos",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.TO_DO,
                        today.minusDays(5),
                        null,
                        null,
                        "Tâche volontairement sans échéance",
                        user),
                task(
                        "Réserver un rendez-vous",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        today.minusDays(10),
                        today.minusDays(3),
                        today.minusDays(4),
                        null,
                        user)));

        InventoryCategory food = createInventoryCategory(
                "Alimentation",
                user);

        InventoryCategory home = createInventoryCategory(
                "Maison",
                user);

        inventoryItemRepository.saveAll(List.of(
                inventoryItem(
                        "Café",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.MISSING,
                        null,
                        food,
                        user),
                inventoryItem(
                        "Riz",
                        InventoryItemType.CONSUMABLE,
                        InventoryStatus.IN_STOCK,
                        null,
                        food,
                        user),
                inventoryItem(
                        "Lampe de bureau",
                        InventoryItemType.DURABLE,
                        InventoryStatus.TO_REPLACE,
                        "Interrupteur défectueux",
                        home,
                        user)));
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

    private ExpenseCategories createMainExpenseCategories(
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

    private void createMainExpenses(
            User user,
            ExpenseCategories categories,
            LocalDate today) {

        YearMonth currentMonth = YearMonth.from(today);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth twoMonthsAgo = currentMonth.minusMonths(2);
        YearMonth fourMonthsAgo = currentMonth.minusMonths(4);
        YearMonth fiveMonthsAgo = currentMonth.minusMonths(5);
        YearMonth sixMonthsAgo = currentMonth.minusMonths(6);

        /*
         * currentMonth - 3 est volontairement vide.
         * Cela permet de vérifier que la moyenne mensuelle historique
         * prend aussi en compte un mois à 0 €.
         */

        List<Expense> expenses = List.of(

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
                        safeCurrentMonthDate(currentMonth, today, 3),
                        categories.groceries(),
                        user),

                expense(
                        "Bus",
                        "24.50",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 6),
                        categories.transport(),
                        user),

                expense(
                        "Restaurant",
                        "31.80",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 10),
                        categories.restaurants(),
                        user),

                expense(
                        "Netflix",
                        "13.49",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 15),
                        categories.subscriptions(),
                        user),

                expense(
                        "Pharmacie",
                        "18.20",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 17),
                        categories.health(),
                        user),

                expense(
                        "Sortie",
                        "22.00",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 18),
                        categories.leisure(),
                        user),

                expense(
                        "Essence complément",
                        "34.00",
                        null,
                        safeCurrentMonthDate(currentMonth, today, 19),
                        categories.transport(),
                        user),

                expense(
                        "Courses complémentaires",
                        "45.60",
                        null,
                        today,
                        categories.groceries(),
                        user),

                expense(
                        "Petit achat",
                        "8.90",
                        null,
                        today,
                        categories.miscellaneous(),
                        user),

                expense(
                        "Déjeuner",
                        "16.50",
                        null,
                        today,
                        categories.restaurants(),
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

    private void createMainTasks(User user, LocalDate today) {

        LocalDate currentWeekMonday = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate previousWeekMonday = currentWeekMonday.minusWeeks(1);

        YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
        YearMonth twoMonthsAgo = YearMonth.from(today).minusMonths(2);

        List<Task> tasks = List.of(

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
                        "Tâche terminée sans échéance",
                        user),

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

                task(
                        "Finaliser une fonctionnalité importante",
                        TaskPriority.URGENT,
                        TaskStatus.COMPLETED,
                        currentWeekMonday.minusDays(3),
                        currentWeekMonday,
                        dateNotAfterToday(currentWeekMonday, today),
                        null,
                        user),

                task(
                        "Mettre à jour la documentation",
                        TaskPriority.IMPORTANT,
                        TaskStatus.COMPLETED,
                        currentWeekMonday.minusDays(7),
                        currentWeekMonday.plusDays(1),
                        dateNotAfterToday(currentWeekMonday.plusDays(1), today),
                        null,
                        user),

                task(
                        "Classer les téléchargements",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        today.minusDays(12),
                        null,
                        today,
                        "Teste completedAt avec dueDate null",
                        user),

                task(
                        "Répondre à un message",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.COMPLETED,
                        today,
                        today,
                        today,
                        "Durée de traitement : 0 jour",
                        user),

                task(
                        "Corriger le bug bloquant",
                        TaskPriority.URGENT,
                        TaskStatus.TO_DO,
                        today.minusDays(3),
                        today.minusDays(1),
                        null,
                        "Tâche urgente en retard",
                        user),

                task(
                        "Envoyer le dossier",
                        TaskPriority.URGENT,
                        TaskStatus.TO_DO,
                        today.minusDays(2),
                        today.plusDays(1),
                        null,
                        null,
                        user),

                task(
                        "Appeler le service client",
                        TaskPriority.URGENT,
                        TaskStatus.TO_DO,
                        today.minusDays(1),
                        null,
                        null,
                        "Urgente sans échéance",
                        user),

                task(
                        "Préparer la prochaine fonctionnalité",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        today,
                        today.plusDays(2),
                        null,
                        null,
                        user),

                task(
                        "Mettre à jour le README",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        today.minusDays(1),
                        today.plusDays(5),
                        null,
                        null,
                        user),

                task(
                        "Comparer les offres internet",
                        TaskPriority.IMPORTANT,
                        TaskStatus.TO_DO,
                        today.minusDays(4),
                        null,
                        null,
                        "Importante sans échéance",
                        user),

                task(
                        "Lire une documentation technique",
                        TaskPriority.NON_CRITICAL,
                        TaskStatus.TO_DO,
                        today.minusDays(2),
                        today.plusDays(3),
                        null,
                        null,
                        user),

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

    private void createMainInventory(User user) {

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
                        "À acheter",
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
                        InventoryStatus.LOW_STOCK,
                        "Prévoir un nouveau bidon",
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
                        "Chargeur USB-C",
                        InventoryItemType.DURABLE,
                        InventoryStatus.NEED_MORE,
                        "Prévoir un chargeur supplémentaire",
                        electronics,
                        user),

                inventoryItem(
                        "Clavier",
                        InventoryItemType.DURABLE,
                        InventoryStatus.TO_REPLACE,
                        "Plusieurs touches répondent mal",
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

    private LocalDate safeCurrentMonthDate(
            YearMonth month,
            LocalDate today,
            int requestedDay) {

        int day = Math.min(requestedDay, today.getDayOfMonth());

        return month.atDay(day);
    }

    private LocalDate dateNotAfterToday(
            LocalDate date,
            LocalDate today) {

        return date.isAfter(today)
                ? today
                : date;
    }

    private void printCredentials() {

        System.out.println("----------------------------------------");
        System.out.println("Davexo development data initialized");
        System.out.println();

        System.out.println("Main user:");
        System.out.println("Email: " + DEV_EMAIL);
        System.out.println("Password: " + DEV_PASSWORD);
        System.out.println();

        System.out.println("Secondary user:");
        System.out.println("Email: " + SECONDARY_EMAIL);
        System.out.println("Password: " + SECONDARY_PASSWORD);
        System.out.println();

        System.out.println("Admin:");
        System.out.println("Email: " + ADMIN_EMAIL);
        System.out.println("Password: " + ADMIN_PASSWORD);
        System.out.println();

        System.out.println("Disabled user:");
        System.out.println("Email: " + DISABLED_EMAIL);
        System.out.println("Password: " + DISABLED_PASSWORD);

        System.out.println("----------------------------------------");
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