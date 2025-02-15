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
    private  static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private  HubRepository hubRepository;
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
            user.setUsername("ahmad");
            user.setPictureUrl("default_profile.jpg");
            user.setPassword(new BCryptPasswordEncoder().encode("password"));
            user.setRole(Roles.User);
            userRepository.save(user);
            logger.info("User table seeded.");
        }
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
        logger.info("User found for bank card: " + user.getCivilId());
        Optional<HubEntity> hubOptional = hubRepository.findFirstByOrderByIdAsc();
        if (!hubOptional.isPresent()) {
            logger.warn("No hub found. Skipping bank card seeding.");
            return;
        }
        HubEntity hub = hubOptional.get();

// Bank Cards Seeding
if (bankCardRepository.count() > 0) {
    logger.info("Bank Cards table already seeded. Skipping...");
} else {
    // Primary Salary Account (from HEAD)
    BankCardEntity salaryCard = new BankCardEntity();
    salaryCard.setCardBalance(850.0);  // Salary in KWD (after deductions and transfers)
    salaryCard.setCardNumber("4565841523654789");
    salaryCard.setBankName("Boubyan Bank");
    salaryCard.setCardType("Salary Account");
    salaryCard.setCvv("123");
    salaryCard.setAccountNumber("0044556677");
    salaryCard.setExpiryDate("05/27");
    salaryCard.setHub(hub);
    salaryCard.setUser(user);
    bankCardRepository.save(salaryCard);

    // Nomo Account (from HEAD)
    BankCardEntity nomoCard = new BankCardEntity();
    nomoCard.setCardBalance(2500.0);  // Savings in KWD
    nomoCard.setCardNumber("4565841523654790");
    nomoCard.setBankName("Nomo Bank");
    nomoCard.setCardType("Digital Account");
    nomoCard.setCvv("456");
    nomoCard.setAccountNumber("0044556678");
    nomoCard.setExpiryDate("08/27");
    nomoCard.setUser(user);
    nomoCard.setHub(hub);
    bankCardRepository.save(nomoCard);

    // Boubyan Youth Account (from HEAD)
    BankCardEntity youthCard = new BankCardEntity();
    youthCard.setCardBalance(350.0);  // Spending money in KWD
    youthCard.setCardNumber("4565841523654791");
    youthCard.setBankName("Boubyan Bank");
    youthCard.setCardType("Youth Account");
    youthCard.setCvv("789");
    youthCard.setAccountNumber("0044556679");
    youthCard.setExpiryDate("11/26");
    youthCard.setUser(user);
    youthCard.setHub(hub);
    bankCardRepository.save(youthCard);

    // Additional Bank Card (from remote branch)
    BankCardEntity card = new BankCardEntity();
    card.setCardBalance(15000.0);
    card.setCardNumber("1234567890123456");
    card.setBankName("Boubyan VISA");
    card.setCardType("checking");
    card.setCvv("222");
    card.setAccountNumber("4433547405");
    card.setExpiryDate("12/30");
    card.setSelected(true);
    card.setHub(hub);
    card.setUser(user);
    bankCardRepository.save(card);

    BankCardEntity card1 = new BankCardEntity();
    card1.setCardBalance(50000.0);
    card1.setCardNumber("1234567890123457");
    card1.setBankName("Nomo VISA");
    card1.setCardType("savings");
    card1.setCvv("222");
    card1.setAccountNumber("4433547401");
    card1.setExpiryDate("12/30");
    card1.setUser(user);
    card1.setHub(hub);
    bankCardRepository.save(card1);

    logger.info("Bank Cards table seeded successfully.");
}

// Hub Seeding (unchanged)
private void seedHubTable(){
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

// Wallet Seeding (unchanged)
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
    logger.info("User found for wallet: " + user.getCivilId());
    List<BankCardEntity> bankCards = bankCardRepository.findAll();
    if (bankCards.isEmpty()) {
        logger.warn("No bank cards found. Skipping wallet seeding.");
        return;
    }
    BankCardEntity bankCard = bankCards.get(0);

    // Fuel Wallet
    WalletEntity fuelWallet = new WalletEntity();
    fuelWallet.setLinkedCards(List.of(bankCard));
    fuelWallet.setAllocation(50.0);  // Monthly allocation in KWD
    fuelWallet.setBalance(35.0);     // Current balance in KWD
    fuelWallet.setCategory("Fuel");
    fuelWallet.setName("Car Fuel");
    fuelWallet.setSelected(true);
    fuelWallet.setHub(user.getHub());
    fuelWallet.setPatternId(1L);
    fuelWallet.setColorId(1L);
    walletRepository.save(fuelWallet);

    // Dining Wallet
    WalletEntity diningWallet = new WalletEntity();
    diningWallet.setLinkedCards(List.of(bankCard));
    diningWallet.setAllocation(150.0);
    diningWallet.setBalance(85.0);
    diningWallet.setCategory("Dining");
    diningWallet.setName("Restaurants & Cafes");
    diningWallet.setSelected(false);
    diningWallet.setHub(user.getHub());
    diningWallet.setPatternId(2L);
    diningWallet.setColorId(2L);
    walletRepository.save(diningWallet);

    // Entertainment Wallet
    WalletEntity entertainmentWallet = new WalletEntity();
    entertainmentWallet.setLinkedCards(List.of(bankCard));
    entertainmentWallet.setAllocation(100.0);
    entertainmentWallet.setBalance(45.0);
    entertainmentWallet.setCategory("Entertainment");
    entertainmentWallet.setName("Movies & Activities");
    entertainmentWallet.setSelected(false);
    entertainmentWallet.setHub(user.getHub());
    entertainmentWallet.setPatternId(3L);
    entertainmentWallet.setColorId(3L);
    walletRepository.save(entertainmentWallet);

    // Shopping Wallet
    WalletEntity shoppingWallet = new WalletEntity();
    shoppingWallet.setLinkedCards(List.of(bankCard));
    shoppingWallet.setAllocation(200.0);
    shoppingWallet.setBalance(120.0);
    shoppingWallet.setCategory("Shopping");
    shoppingWallet.setName("Avenues Mall");
    shoppingWallet.setSelected(false);
    shoppingWallet.setHub(user.getHub());
    shoppingWallet.setPatternId(4L);
    shoppingWallet.setColorId(4L);
    walletRepository.save(shoppingWallet);

    logger.info("Wallet table seeded successfully.");
}

// Transactions Seeding
private void seedTransactionsTable() {
    logger.info("Checking transactions seeding...");

    if (transactionsRepository.count() > 0) {
        logger.info("Transactions table already seeded. Skipping...");
        return;
    }

    Optional<UserEntity> userOptional = userRepository.findFirstByOrderByIdAsc();
    if (!userOptional.isPresent()) {
        logger.warn("No users found. Skipping transactions seeding.");
        return;
    }

    UserEntity user = userOptional.get();
    logger.info("User found for transactions: " + user.getCivilId());

    List<WalletEntity> wallets = walletRepository.findAll();
    if (wallets.isEmpty()) {
        logger.warn("No wallets found. Skipping transactions seeding.");
        return;
    }
    // Use the first wallet for seeding transactions
    WalletEntity wallet = wallets.get(0);
    logger.info("Wallet found for transactions: " + wallet.getName());

    // Create generic transactions (from remote branch)
    for (int i = 1; i <= 30; i++) {
        TransactionsEntity transaction = new TransactionsEntity();
        transaction.setAmount(50.0 + (i * 5));
        transaction.setTransactionName("Transaction #" + i);
        transaction.setWalletUsed(wallet);
        transaction.setHub(wallet.getHub());
        transactionsRepository.save(transaction);
    }

    // Fuel Transactions (from HEAD)
    TransactionsEntity fuelTransaction1 = new TransactionsEntity();
    fuelTransaction1.setAmount(12.500);  // Full tank for medium car
    fuelTransaction1.setTransactionName("KNPC - Fuel Station Shuwaikh");
    fuelTransaction1.setWalletUsed(wallet);
    fuelTransaction1.setHub(wallet.getHub());
    fuelTransaction1.setLatitude(29.3399);
    fuelTransaction1.setLongitude(47.9337);
    transactionsRepository.save(fuelTransaction1);

    TransactionsEntity fuelTransaction2 = new TransactionsEntity();
    fuelTransaction2.setAmount(8.750);  // Partial tank
    fuelTransaction2.setTransactionName("KNPC - Fuel Station Jahra Road");
    fuelTransaction2.setWalletUsed(wallet);
    fuelTransaction2.setHub(wallet.getHub());
    fuelTransaction2.setLatitude(29.3375);
    fuelTransaction2.setLongitude(47.6581);
    transactionsRepository.save(fuelTransaction2);

    TransactionsEntity fuelTransaction3 = new TransactionsEntity();
    fuelTransaction3.setAmount(15.250);  // Full tank for SUV
    fuelTransaction3.setTransactionName("KNPC - Fuel Station Gulf Road");
    fuelTransaction3.setWalletUsed(wallet);
    fuelTransaction3.setHub(wallet.getHub());
    fuelTransaction3.setLatitude(29.3578);
    fuelTransaction3.setLongitude(48.0091);
    transactionsRepository.save(fuelTransaction3);

    logger.info("Transactions table seeded successfully.");
}

        // Dining Transactions
        WalletEntity diningWallet = wallets.get(1);
        
        // High-end dining
        TransactionsEntity diningTransaction1 = new TransactionsEntity();
        diningTransaction1.setAmount(45.500);  // Dinner for two
        diningTransaction1.setTransactionName("Mais Alghanim - Gulf Road");
        diningTransaction1.setWalletUsed(diningWallet);
        diningTransaction1.setHub(diningWallet.getHub());
        diningTransaction1.setLatitude(29.3489);
        diningTransaction1.setLongitude(48.0264);
        transactionsRepository.save(diningTransaction1);

        // Casual dining
        TransactionsEntity diningTransaction2 = new TransactionsEntity();
        diningTransaction2.setAmount(8.250);
        diningTransaction2.setTransactionName("Starbucks - Kuwait City");
        diningTransaction2.setWalletUsed(diningWallet);
        diningTransaction2.setHub(diningWallet.getHub());
        diningTransaction2.setLatitude(29.3759);
        diningTransaction2.setLongitude(47.9774);
        transactionsRepository.save(diningTransaction2);

        // Local cuisine
        TransactionsEntity diningTransaction3 = new TransactionsEntity();
        diningTransaction3.setAmount(12.750);
        diningTransaction3.setTransactionName("Canary - Salmiya");
        diningTransaction3.setWalletUsed(diningWallet);
        diningTransaction3.setHub(diningWallet.getHub());
        diningTransaction3.setLatitude(29.3331);
        diningTransaction3.setLongitude(48.0258);
        transactionsRepository.save(diningTransaction3);

        // Fast food
        TransactionsEntity diningTransaction4 = new TransactionsEntity();
        diningTransaction4.setAmount(6.500);
        diningTransaction4.setTransactionName("Shake Shack - The Avenues");
        diningTransaction4.setWalletUsed(diningWallet);
        diningTransaction4.setHub(diningWallet.getHub());
        diningTransaction4.setLatitude(29.3015);
        diningTransaction4.setLongitude(47.9282);
        transactionsRepository.save(diningTransaction4);

        // Entertainment Transactions
        WalletEntity entertainmentWallet = wallets.get(2);
        
        // Cinema
        TransactionsEntity entertainmentTransaction1 = new TransactionsEntity();
        entertainmentTransaction1.setAmount(18.000);  // VIP movie tickets
        entertainmentTransaction1.setTransactionName("Cinescape - The Avenues IMAX");
        entertainmentTransaction1.setWalletUsed(entertainmentWallet);
        entertainmentTransaction1.setHub(entertainmentWallet.getHub());
        entertainmentTransaction1.setLatitude(29.3015);
        entertainmentTransaction1.setLongitude(47.9282);
        transactionsRepository.save(entertainmentTransaction1);

        // Entertainment City
        TransactionsEntity entertainmentTransaction2 = new TransactionsEntity();
        entertainmentTransaction2.setAmount(35.000);  // Family entertainment
        entertainmentTransaction2.setTransactionName("Kuwait Magic Mall - Entertainment City");
        entertainmentTransaction2.setWalletUsed(entertainmentWallet);
        entertainmentTransaction2.setHub(entertainmentWallet.getHub());
        entertainmentTransaction2.setLatitude(29.3608);
        entertainmentTransaction2.setLongitude(47.9169);
        transactionsRepository.save(entertainmentTransaction2);

        // Cultural event
        TransactionsEntity entertainmentTransaction3 = new TransactionsEntity();
        entertainmentTransaction3.setAmount(25.000);
        entertainmentTransaction3.setTransactionName("Kuwait Opera House - Concert Ticket");
        entertainmentTransaction3.setWalletUsed(entertainmentWallet);
        entertainmentTransaction3.setHub(entertainmentWallet.getHub());
        entertainmentTransaction3.setLatitude(29.3401);
        entertainmentTransaction3.setLongitude(48.0261);
        transactionsRepository.save(entertainmentTransaction3);

        // Shopping Transactions
        WalletEntity shoppingWallet = wallets.get(3);
        
        // Luxury shopping
        TransactionsEntity shoppingTransaction1 = new TransactionsEntity();
        shoppingTransaction1.setAmount(185.000);
        shoppingTransaction1.setTransactionName("Gucci - The Avenues Prestige");
        shoppingTransaction1.setWalletUsed(shoppingWallet);
        shoppingTransaction1.setHub(shoppingWallet.getHub());
        shoppingTransaction1.setLatitude(29.3015);
        shoppingTransaction1.setLongitude(47.9282);
        transactionsRepository.save(shoppingTransaction1);

        // Regular retail
        TransactionsEntity shoppingTransaction2 = new TransactionsEntity();
        shoppingTransaction2.setAmount(65.500);
        shoppingTransaction2.setTransactionName("Zara - The Avenues");
        shoppingTransaction2.setWalletUsed(shoppingWallet);
        shoppingTransaction2.setHub(shoppingWallet.getHub());
        shoppingTransaction2.setLatitude(29.3015);
        shoppingTransaction2.setLongitude(47.9282);
        transactionsRepository.save(shoppingTransaction2);

        // Electronics
        TransactionsEntity shoppingTransaction3 = new TransactionsEntity();
        shoppingTransaction3.setAmount(299.000);
        shoppingTransaction3.setTransactionName("Xcite - Al Rai");
        shoppingTransaction3.setWalletUsed(shoppingWallet);
        shoppingTransaction3.setHub(shoppingWallet.getHub());
        shoppingTransaction3.setLatitude(29.3286);
        shoppingTransaction3.setLongitude(47.9216);
        transactionsRepository.save(shoppingTransaction3);

        // Grocery shopping
        TransactionsEntity shoppingTransaction4 = new TransactionsEntity();
        shoppingTransaction4.setAmount(42.750);
        shoppingTransaction4.setTransactionName("Sultan Center - Salmiya");
        shoppingTransaction4.setWalletUsed(shoppingWallet);
        shoppingTransaction4.setHub(shoppingWallet.getHub());
        shoppingTransaction4.setLatitude(29.3331);
        shoppingTransaction4.setLongitude(48.0258);
        transactionsRepository.save(shoppingTransaction4);

        // Sports equipment
        TransactionsEntity shoppingTransaction5 = new TransactionsEntity();
        shoppingTransaction5.setAmount(89.000);
        shoppingTransaction5.setTransactionName("Decathlon - The Avenues");
        shoppingTransaction5.setWalletUsed(shoppingWallet);
        shoppingTransaction5.setHub(shoppingWallet.getHub());
        shoppingTransaction5.setLatitude(29.3015);
        shoppingTransaction5.setLongitude(47.9282);
        transactionsRepository.save(shoppingTransaction5);

        logger.info("Transactions table seeded successfully with realistic Kuwaiti spending patterns.");
    }
}

