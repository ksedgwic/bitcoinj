package com.google.bitcoin.core;

import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.wallet.KeyBag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TransactionSigner {

    boolean requestSignature(Transaction.SigHash hashType,
                             boolean anyoneCanPay,
                             Transaction transaction,
                             Set<Transaction> inputTransactions);

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

/**
 * @author devrandom
 */
public interface TransactionSigner {
    public interface RegistrationListener {
        void phoneTypeDetected(String phoneId, boolean landline);
        /** code to display to the user in order to validate the line */
        void landlinePhoneCode(String phoneId, String code);
        /** The registration is complete, and {@link com.google.bitcoin.core.TransactionSigner#getServiceKeys()} may now be called */
        void registrationComplete();
    }

    /** Override default API endpoint */
    void setEndpoint(URI uri);

    /**
     * Register with signing service, if required
     *
     * @param accountKeys public keys for wallet, recovery.  All derivation levels under
     *                    this should be non-hardened.
     * @param pii personal information if any, to be provided to service during registration
     * @param parameters any parameters that customize the way the service will work with this wallet
     */
    void register(List<DeterministicKey> accountKeys,
                  Object pii,
                  Object parameters);

    /** Connect to existing registration */
    void connect(DeterministicKey accountKey);

    /** Keys provided by the service, to marry with the user keys.  Can be called after registration is complete. */
    List<DeterministicKey> getServiceKeys();

    /** Serialize this signer */
    void serialize(OutputStream stream);

    /** Deserialize this signer */
    void deserialize(InputStream stream);

    public enum VerificationType {
        OTP,
        SMS,
        CALL,
        CAPTCHA
    }

    /** Get next fee destination address */
    Address getFeeAddress();

    /** Get the amount of fee required
     *
     * @param amount the amount that will be sent from the wallet
     * @return the fee required for the amount
     */
    long getFeeForAmount(long amount);

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
