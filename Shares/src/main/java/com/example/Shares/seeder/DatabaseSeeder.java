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
            user.setCivilId("293082501504");
            user.setPhoneNumber("+96599528332");
            user.setRole(Roles.User);
            user.setUsername("postgres");
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

        BankCardEntity card = new BankCardEntity();
        card.setCardBalance(15000.0);
        card.setCardNumber("1234567890123456");
        card.setBankName("Boubyan VISA");
        card.setCardType("checking");
        card.setCvv("222");
        card.setAccountNumber("4433547405");
        card.setExpiryDate("12/30");
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
        bankCardRepository.save(card1);

        logger.info("Bank Cards table seeded successfully.");
    }


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
        hub.setHubCardNumber("2224567890123456");

        hub.setUser(user);
        hubRepository.save(hub);

        logger.info("Hub table seeded successfully.");
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
        logger.info("User found for wallet: " + user.getCivilId());
        Optional<BankCardEntity> bankCardOptional = bankCardRepository.findByCardNumber("1234567890123456");
        BankCardEntity bankCard = bankCardOptional.get();

        WalletEntity wallet = new WalletEntity();
        wallet.setLinkedCards(List.of(bankCard));
        wallet.setAllocation(1000.0);
        wallet.setBalance(1000.0);
        wallet.setCategory("E-shopping");
        wallet.setName("Shopping wallet");
        wallet.setHub(user.getHub());
        wallet.setPatternId(5L);
        wallet.setColorId(4L);
        walletRepository.save(wallet);

        logger.info("Wallet table seeded successfully.");
    }


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

        WalletEntity wallet = walletRepository.findFirstByOrderByIdAsc().get();
        logger.info("Wallet found for transactions: " + wallet.getName());

        TransactionsEntity transaction = new TransactionsEntity();
        transaction.setAmount(100.0);
        transaction.setTransactionName("Netflix subscription");
        transaction.setWalletUsed(wallet);
        transaction.setHub(wallet.getHub());

        transactionsRepository.save(transaction);

        logger.info("Transactions table seeded successfully.");
    }
}

