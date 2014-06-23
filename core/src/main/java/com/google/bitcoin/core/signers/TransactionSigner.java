package com.google.bitcoin.core.signers;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.wallet.KeyBag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author devrandom
 */
public interface TransactionSigner {
    /** Override default API endpoint */
    void setEndpoint(URI uri);

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

    /**
     * Request signing of a transaction
     *
     * @param hashType
     * @param anyoneCanPay
     * @param transaction the transaction
     * @param requestId unique request ID for this spend
     * @param inputTransactions input transactions to enable miner fee calculations
     * @param accountKeys
     * @param verifications SMS, OTP and other such
     * @param keyBag a way to get keys involved in this transaction
     * @return whether this signer was able to handle this transaction
     */
    boolean sendSignatureRequest(Transaction.SigHash hashType,
                                 boolean anyoneCanPay,
                                 Transaction transaction,
                                 String requestId, // make this mandatory and skip the returned ID?
                                 Set<Transaction> inputTransactions,
                                 List<DeterministicKey> accountKeys,
                                 Map<VerificationType, String> verifications,
                                 KeyBag keyBag) throws IOException;

    /** Cancels a signature request */
    boolean cancelSignatureRequest(String requestId) throws IOException;

    /** Polls the status of a signature request.  A callback will be triggered */
    String checkSignatureRequest(String requestId) throws IOException;

    void addListener(SignatureRequestListener listener);

    /**
     * Signature state listener
     */
    public interface SignatureRequestListener {
        void onComplete(String requestId, Transaction tx);
        void onDeferred(String requestId,
                        Set<VerificationType> requiredVerifications,
                        long untilMillis);
        void onCanceled(String requestId, String reason);
    }
}
