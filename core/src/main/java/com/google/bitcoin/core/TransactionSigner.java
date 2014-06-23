package com.google.bitcoin.core;

import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.wallet.KeyBag;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author devrandom
 */
public interface TransactionSigner {
    // What you need to know in general
    // - wallet master pubkey
    // - signer API endpoint

    // What do you need to tell the wallet
    // - external signer master pubkey

    /**
     *
     * @param masterKeys public keys for wallet, recovery
     * @param pii
     * @param parameters
     */
    public void signup(List<DeterministicKey> masterKeys,
                       Object pii,
                       Object parameters);

    public enum VerificationType {
        OTP,
        SMS,
        CALL,
        CAPTCHA
    }

    /**
     *
     * @param hashType
     * @param anyoneCanPay
     * @param transaction the transaction
     * @param requestId unique request ID for this spend
     * @param inputTransactions input transactions to enable miner fee calculations
     * @param masterKeys
     * @param verifications SMS, OTP and other such
     * @param keyBag a way to get keys involved in this transaction
     * @return whether this signer was able to handle this transaction
     */
    boolean sendSignatureRequest(Transaction.SigHash hashType,
                                 boolean anyoneCanPay,
                                 Transaction transaction,
                                 String requestId, // make this mandatory and skip the returned ID?
                                 Set<Transaction> inputTransactions,
                                 List<DeterministicKey> masterKeys,
                                 Map<VerificationType, String> verifications,
                                 KeyBag keyBag) throws IOException;
    String cancelRequest(String requestId) throws IOException;
    String check(String requestId) throws IOException;

    void addListener(TransactionRequestListener listener);

    /**
     * Transaction state listener
     */
    public interface TransactionRequestListener {
        void transactionComplete(String requestId, Transaction tx);
        void transactionDeferred(String requestId,
                                 Set<VerificationType> requiredVerifications,
                                 long untilMillis);
        void transactionCanceled(String requestId, String reason);
    }
}