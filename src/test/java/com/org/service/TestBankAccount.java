package com.org.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankAccount (Builder pattern)")
class TestBankAccount {

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("builds with all fields set")
        void buildsWithAllFields() {
            BankAccount acc = BankAccount.builder()
                .accountId("ACC-001")
                .initialBalance(500.0)
                .overdraftLimit(100.0)
                .build();

            assertAll(
                () -> assertEquals("ACC-001", acc.getAccountId()),
                () -> assertEquals(500.0, acc.getBalance(), 0.001),
                () -> assertEquals(600.0, acc.getAvailableBalance(), 0.001)
            );
        }

        @Test
        @DisplayName("defaults: balance=0, overdraft=0")
        void defaultsAreZero() {
            BankAccount acc = BankAccount.builder().accountId("ACC-002").build();
            assertAll(
                () -> assertEquals(0.0, acc.getBalance(), 0.001),
                () -> assertEquals(0.0, acc.getAvailableBalance(), 0.001)
            );
        }

        @Test
        @DisplayName("blank accountId throws — kills NULL_RETURNS mutant")
        void blankIdThrows() {
            assertThrows(IllegalStateException.class,
                () -> BankAccount.builder().accountId("  ").build());
        }

        @Test
        @DisplayName("null accountId throws")
        void nullIdThrows() {
            assertThrows(IllegalStateException.class,
                () -> BankAccount.builder().build());
        }

        @Test
        @DisplayName("negative initial balance throws")
        void negativeBalanceThrows() {
            assertThrows(IllegalArgumentException.class,
                () -> BankAccount.builder().accountId("X").initialBalance(-1).build());
        }

        @Test
        @DisplayName("negative overdraft limit throws")
        void negativeOverdraftThrows() {
            assertThrows(IllegalArgumentException.class,
                () -> BankAccount.builder().accountId("X").overdraftLimit(-1).build());
        }
    }

    // -------------------------------------------------------------------------
    // deposit — verifies exact balance after each deposit (kills MATH mutants)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("deposit")
    class Deposit {

        private BankAccount account;

        @BeforeEach
        void setUp() {
            account = BankAccount.builder().accountId("DEP-01").initialBalance(100.0).build();
        }

        @ParameterizedTest(name = "deposit {0} on 100.0 → {1}")
        @CsvSource({"50.0, 150.0", "0.01, 100.01", "1000.0, 1100.0"})
        void deposit(double amount, double expectedBalance) {
            account.deposit(amount);
            assertEquals(expectedBalance, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("zero deposit throws — kills CONDITIONALS_BOUNDARY mutant (> vs >=)")
        void zeroDepositThrows() {
            assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
            assertEquals(100.0, account.getBalance(), 0.001);  // state unchanged
        }

        @ParameterizedTest(name = "invalid deposit amount {0} throws")
        @ValueSource(doubles = {-1.0, -0.01, Double.NEGATIVE_INFINITY})
        void negativeDepositThrows(double amount) {
            assertThrows(IllegalArgumentException.class, () -> account.deposit(amount));
        }
    }

    // -------------------------------------------------------------------------
    // withdraw — verifies balance AND canWithdraw boundary
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("reduces balance by exact amount")
        void reducesBalance() {
            BankAccount acc = BankAccount.builder().accountId("W-1").initialBalance(200.0).build();
            acc.withdraw(80.0);
            assertEquals(120.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("withdraw entire balance — boundary: balance after = 0")
        void withdrawEntireBalance() {
            BankAccount acc = BankAccount.builder().accountId("W-2").initialBalance(100.0).build();
            acc.withdraw(100.0);
            assertEquals(0.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("withdraw using overdraft — kills MATH mutant in getAvailableBalance")
        void withdrawFromOverdraft() {
            BankAccount acc = BankAccount.builder()
                .accountId("W-3").initialBalance(100.0).overdraftLimit(50.0).build();
            acc.withdraw(150.0);
            assertEquals(-50.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("withdraw one over available balance throws — state unchanged")
        void withdrawExceedsAvailableThrows() {
            BankAccount acc = BankAccount.builder()
                .accountId("W-4").initialBalance(100.0).overdraftLimit(50.0).build();
            assertThrows(IllegalStateException.class, () -> acc.withdraw(150.01));
            assertEquals(100.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("zero withdraw throws")
        void zeroWithdrawThrows() {
            BankAccount acc = BankAccount.builder().accountId("W-5").initialBalance(100.0).build();
            assertThrows(IllegalArgumentException.class, () -> acc.withdraw(0));
        }
    }

    // -------------------------------------------------------------------------
    // canWithdraw — kills boundary >= vs > mutant
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("canWithdraw")
    class CanWithdraw {

        @ParameterizedTest(name = "balance={0} overdraft={1} amount={2} canWithdraw={3}")
        @CsvSource({
            "100.0,  0.0, 100.0, true",     // exactly equal — kills >= vs > mutant
            "100.0,  0.0, 100.1, false",    // one cent over
            "100.0, 50.0, 150.0, true",     // exactly uses overdraft
            "100.0, 50.0, 150.1, false",    // exceeds combined limit
            "  0.0,  0.0,   0.1, false"     // empty account
        })
        void canWithdraw(double balance, double overdraft, double amount, boolean expected) {
            BankAccount acc = BankAccount.builder()
                .accountId("CW").initialBalance(balance).overdraftLimit(overdraft).build();
            assertEquals(expected, acc.canWithdraw(amount));
        }
    }

    // -------------------------------------------------------------------------
    // transfer — verifies both accounts' state change
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("transfer")
    class Transfer {

        @Test
        @DisplayName("transfers exact amount between accounts")
        void transfer() {
            BankAccount source = BankAccount.builder().accountId("SRC").initialBalance(500.0).build();
            BankAccount target = BankAccount.builder().accountId("TGT").initialBalance(100.0).build();

            source.transfer(target, 200.0);

            assertAll(
                () -> assertEquals(300.0, source.getBalance(), 0.001),
                () -> assertEquals(300.0, target.getBalance(), 0.001)
            );
        }

        @Test
        @DisplayName("transfer to null target throws")
        void nullTargetThrows() {
            BankAccount acc = BankAccount.builder().accountId("SRC").initialBalance(100.0).build();
            assertThrows(IllegalArgumentException.class, () -> acc.transfer(null, 10.0));
            assertEquals(100.0, acc.getBalance(), 0.001);  // source unchanged
        }

        @Test
        @DisplayName("insufficient funds in source throws — both accounts unchanged")
        void insufficientFundsThrows() {
            BankAccount source = BankAccount.builder().accountId("SRC").initialBalance(50.0).build();
            BankAccount target = BankAccount.builder().accountId("TGT").initialBalance(100.0).build();

            assertThrows(IllegalStateException.class, () -> source.transfer(target, 100.0));

            assertAll(
                () -> assertEquals(50.0, source.getBalance(), 0.001),
                () -> assertEquals(100.0, target.getBalance(), 0.001)
            );
        }
    }

    // -------------------------------------------------------------------------
    // applyInterest — kills MATH, RETURN_VALUES mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("applyInterest")
    class ApplyInterest {

        @ParameterizedTest(name = "balance={0} rate={1}% interest={2} newBalance={3}")
        @CsvSource({
            "1000.0,  5.0,  50.0, 1050.0",
            "1000.0, 10.0, 100.0, 1100.0",
            "1000.0,  0.0,   0.0, 1000.0",   // zero rate — kills MATH mutant
            " 500.0,  2.0,  10.0,  510.0"
        })
        void applyInterest(double balance, double rate, double expectedInterest, double expectedBalance) {
            BankAccount acc = BankAccount.builder().accountId("INT").initialBalance(balance).build();
            double interest = acc.applyInterest(rate);
            assertAll(
                () -> assertEquals(expectedInterest, interest, 0.001),
                () -> assertEquals(expectedBalance, acc.getBalance(), 0.001)
            );
        }

        @Test
        @DisplayName("negative rate throws")
        void negativeRateThrows() {
            BankAccount acc = BankAccount.builder().accountId("INT").initialBalance(100.0).build();
            assertThrows(IllegalArgumentException.class, () -> acc.applyInterest(-1));
            assertEquals(100.0, acc.getBalance(), 0.001);  // state unchanged
        }
    }
}
