package com.example.Shares.seeder;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.repository.BankCardRepository;
import com.example.Shares.auth.repository.UserRepository;
import com.example.Shares.auth.utils.Roles;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.hub.repository.HubRepository;
import com.example.Shares.transactions.entity.TransactionsEntity;
import com.example.Shares.transactions.repository.TransactionsRepository;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.repository.WalletRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

@Component
public class DatabaseSeeder implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private HubRepository hubRepository;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Running database seeder...");
        seedUsers();
        seedHubTable();
        seedBankCardTable();
        seedWalletTable();
        seedTransactionsTable();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            UserEntity user = new UserEntity();
            user.setCivilId("299010100494");  // Kuwaiti Civil ID format
            user.setPhoneNumber("+96555123456");  // Kuwait phone number format
            user.setUsername("jasim_kw");  // Username for login
            user.setFirstName("Jasim");  // First name derived from Civil ID
            user.setLastName("Al-deeb");  // Last name derived from Civil ID
            user.setPictureUrl("default_profile.jpg");
            user.setPassword(new BCryptPasswordEncoder().encode("password"));
            user.setRole(Roles.User);
            userRepository.save(user);
            logger.info("User table seeded.");
        }
    }

    private void seedHubTable() {
        logger.info("Checking hub seeding...");

        if (hubRepository.count() > 0) {
            logger.info("Hub table already seeded. Skipping...");
            return;
        }

        Optional<UserEntity> userOptional = userRepository.findFirstByOrderByIdAsc();
        if (!userOptional.isPresent()) {
            logger.warn("No users found. Skipping hub seeding.");
            return;
        }

        UserEntity user = userOptional.get();
        logger.info("User found for hub: " + user.getCivilId());

        HubEntity hub = new HubEntity();
        hub.setHubCardNumber("6515841523654789");  // Different prefix for hub card
        hub.setUser(user);
        hubRepository.save(hub);

        logger.info("Hub table seeded successfully.");
    }

    private void seedBankCardTable() {
        logger.info("Checking bank card seeding...");

        if (bankCardRepository.count() > 0) {
            logger.info("Bank Cards table already seeded. Skipping...");
            return;
        }

        Optional<UserEntity> userOptional = userRepository.findFirstByOrderByIdAsc();
        if (!userOptional.isPresent()) {
            logger.warn("No users found. Skipping bank card seeding.");
            return;
        }

        UserEntity user = userOptional.get();
        Optional<HubEntity> hubOptional = hubRepository.findFirstByOrderByIdAsc();
        if (!hubOptional.isPresent()) {
            logger.warn("No hub found. Skipping bank card seeding.");
            return;
        }
        HubEntity hub = hubOptional.get();

        // Main Salary Card (Boubyan)
        BankCardEntity salaryCard = new BankCardEntity();
        salaryCard.setCardBalance(1200.0);  // Mid-month salary remaining
        salaryCard.setCardNumber("4565841523654789");
        salaryCard.setBankName("Boubyan Bank");
        salaryCard.setCardType("Salary Account");
        salaryCard.setCvv("123");
        salaryCard.setAccountNumber("0044556677");
        salaryCard.setExpiryDate("05/27");
        salaryCard.setHub(hub);
        salaryCard.setUser(user);
        bankCardRepository.save(salaryCard);

        // Secondary Digital Card (Nomo)
        BankCardEntity nomoCard = new BankCardEntity();
        nomoCard.setCardBalance(850.0);    // Some savings and online spending
        nomoCard.setCardNumber("4565841523654790");
        nomoCard.setBankName("Nomo Bank");
        nomoCard.setCardType("Digital Account");
        nomoCard.setCvv("456");
        nomoCard.setAccountNumber("0044556678");
        nomoCard.setExpiryDate("08/27");
        nomoCard.setUser(user);
        nomoCard.setHub(hub);
        bankCardRepository.save(nomoCard);

        logger.info("Bank Cards table seeded successfully.");
    }

    private void seedWalletTable() {
        logger.info("Checking wallet seeding...");

        if (walletRepository.count() > 0) {
            logger.info("Wallet table already seeded. Skipping...");
            return;
        }

        Optional<UserEntity> userOptional = userRepository.findFirstByOrderByIdAsc();
        if (!userOptional.isPresent()) {
            logger.warn("No users found. Skipping wallet seeding.");
            return;
        }

        UserEntity user = userOptional.get();
        List<BankCardEntity> bankCards = bankCardRepository.findAll();
        if (bankCards.isEmpty()) {
            logger.warn("No bank cards found. Skipping wallet seeding.");
            return;
        }

        BankCardEntity salaryCard = bankCards.get(0);  // Boubyan salary card
        BankCardEntity nomoCard = bankCards.get(1);    // Nomo digital card

        // Bills & Essentials (from salary card)
        WalletEntity billsWallet = new WalletEntity();
        billsWallet.setLinkedCards(List.of(salaryCard));
        billsWallet.setAllocation(250.0);   // Monthly bills
        billsWallet.setBalance(85.0);       // Remaining after paying most bills
        billsWallet.setCategory("Bills");
        billsWallet.setName("Bills & Essentials");
        billsWallet.setSelected(true);
        billsWallet.setHub(user.getHub());
        billsWallet.setPatternId(1L);
        billsWallet.setColorId(1L);
        walletRepository.save(billsWallet);

        // Food & Daily (from salary card)
        WalletEntity foodWallet = new WalletEntity();
        foodWallet.setLinkedCards(List.of(salaryCard));
        foodWallet.setAllocation(250.0);   // Monthly food budget
        foodWallet.setBalance(80.0);       // Mid-month remaining
        foodWallet.setCategory("Food");
        foodWallet.setName("Food & Daily");
        foodWallet.setSelected(false);
        foodWallet.setHub(user.getHub());
        foodWallet.setPatternId(2L);
        foodWallet.setColorId(2L);
        walletRepository.save(foodWallet);

        // Travel & Vacation (using Nomo for online bookings)
        WalletEntity travelWallet = new WalletEntity();
        travelWallet.setLinkedCards(List.of(nomoCard));
        travelWallet.setAllocation(300.0);  // Monthly travel savings
        travelWallet.setBalance(180.0);     // Saved for next trip
        travelWallet.setCategory("Travel");
        travelWallet.setName("Travel & Vacation");
        travelWallet.setSelected(false);
        travelWallet.setHub(user.getHub());
        travelWallet.setPatternId(3L);
        travelWallet.setColorId(3L);
        walletRepository.save(travelWallet);

        // Gadgets & Gaming (using Nomo for online purchases)
        WalletEntity gadgetsWallet = new WalletEntity();
        gadgetsWallet.setLinkedCards(List.of(nomoCard));
        gadgetsWallet.setAllocation(200.0);  // Monthly tech budget
        gadgetsWallet.setBalance(75.0);      // Remaining after purchases
        gadgetsWallet.setCategory("Gadgets");
        gadgetsWallet.setName("Gadgets & Gaming");
        gadgetsWallet.setSelected(false);
        gadgetsWallet.setHub(user.getHub());
        gadgetsWallet.setPatternId(4L);
        gadgetsWallet.setColorId(4L);
        walletRepository.save(gadgetsWallet);

        // Entertainment (using both cards)
        WalletEntity entertainmentWallet = new WalletEntity();
        entertainmentWallet.setLinkedCards(List.of(salaryCard, nomoCard));
        entertainmentWallet.setAllocation(200.0);  // Monthly entertainment
        entertainmentWallet.setBalance(60.0);      // Left for month
        entertainmentWallet.setCategory("Entertainment");
        entertainmentWallet.setName("Entertainment");
        entertainmentWallet.setSelected(false);
        entertainmentWallet.setHub(user.getHub());
        entertainmentWallet.setPatternId(5L);
        entertainmentWallet.setColorId(5L);
        walletRepository.save(entertainmentWallet);

        logger.info("Wallet table seeded successfully with realistic spending data.");
    }

    private void seedTransactionsTable() {
        logger.info("Checking transactions seeding...");

        if (transactionsRepository.count() > 0) {
            logger.info("Transactions table already seeded. Skipping...");
            return;
        }

        List<WalletEntity> wallets = walletRepository.findAll();
        if (wallets.isEmpty()) {
            logger.warn("No wallets found. Skipping transactions seeding.");
            return;
        }

        WalletEntity billsWallet = wallets.get(0);
        seedBillsTransactions(billsWallet);

        WalletEntity foodWallet = wallets.get(1);
        seedFoodTransactions(foodWallet);

        WalletEntity travelWallet = wallets.get(2);
        seedTravelTransactions(travelWallet);

        WalletEntity gadgetsWallet = wallets.get(3);
        seedGadgetsTransactions(gadgetsWallet);

        WalletEntity entertainmentWallet = wallets.get(4);
        seedEntertainmentTransactions(entertainmentWallet);

        logger.info("Transactions table seeded successfully with realistic spending data.");
    }

    private void seedBillsTransactions(WalletEntity wallet) {
        createTransaction(wallet, 20.000, "Netflix", 29.3399, 47.9337);
        createTransaction(wallet, 65.000, "Ministry of Electricity", 29.3759, 47.9774);
        createTransaction(wallet, 35.000, "Zain Kuwait", 29.3015, 47.9282);
        createTransaction(wallet, 45.000, "Ooredoo Internet", 29.3399, 47.9337);
    }

    private void seedFoodTransactions(WalletEntity wallet) {
        createTransaction(wallet, 8.500, "Pick - The Avenues", 29.3759, 47.9774);
        createTransaction(wallet, 15.500, "Caribou - Arraya", 29.3759, 47.9774);
        createTransaction(wallet, 18.750, "Table Otto - JACC", 29.3780, 47.9903);
        createTransaction(wallet, 25.000, "Dar Hamad - Gulf Road", 29.3420, 48.2203);
        createTransaction(wallet, 12.750, "Shake Shack - Al Kout", 29.0965, 48.1301);
    }

    private void seedTravelTransactions(WalletEntity wallet) {
        createTransaction(wallet, 85.000, "Kuwait Airways", 29.2268, 47.9689);
        createTransaction(wallet, 120.000, "Booking.com", 29.3759, 47.9774);
        createTransaction(wallet, 35.000, "Dubai Visa Center", 29.3759, 47.9774);
        createTransaction(wallet, 25.000, "AXA Insurance", 29.3759, 47.9774);
        createTransaction(wallet, 15.000, "Uber Dubai", 29.3759, 47.9774);
    }

    private void seedGadgetsTransactions(WalletEntity wallet) {
        createTransaction(wallet, 65.000, "Xcite Electronics", 29.3759, 47.9774);
        createTransaction(wallet, 45.000, "Virgin - Avenues", 29.3759, 47.9774);
        createTransaction(wallet, 25.000, "PlayStation Store", 29.3759, 47.9774);
        createTransaction(wallet, 35.000, "Blink Kuwait", 29.3759, 47.9774);
        createTransaction(wallet, 15.000, "Apple Store", 29.3759, 47.9774);
    }

    private void seedEntertainmentTransactions(WalletEntity wallet) {
        createTransaction(wallet, 75.000, "VOX Cinema - Avenues", 29.2694, 47.9783);
        createTransaction(wallet, 35.000, "Cinescape - The Gate", 29.3483, 47.9371);
        createTransaction(wallet, 25.000, "Nintendo Store", 29.3608, 47.9169);
        createTransaction(wallet, 35.000, "Grand Cafe - Kuwait", 29.3015, 47.9282);
    }

    private void createTransaction(WalletEntity wallet, double amount, String name, double lat, double lon) {
        TransactionsEntity transaction = new TransactionsEntity();
        transaction.setAmount(amount);
        transaction.setTransactionName(name);
        transaction.setWalletUsed(wallet);
        transaction.setHub(wallet.getHub());
        transaction.setLatitude(lat);
        transaction.setLongitude(lon);
        transactionsRepository.save(transaction);
    }
}

