package com.google.bitcoin.core;

import java.io.IOException;
import java.util.UUID;

public interface TransactionSigner {
    public class ProposedTransaction {
        private final Transaction.SigHash hashType;
        private final boolean anyoneCanPay;
        private final Transaction partialTransaction;
        private final UUID proposalId; // Randomly generated

        /**
         * Used by deserialization.
         */
        public ProposedTransaction(Transaction.SigHash hashType,
                                   boolean anyoneCanPay,
                                   Transaction partialTransaction,
                                   UUID proposalId) {
            this.hashType = hashType;
            this.anyoneCanPay = anyoneCanPay;
            this.partialTransaction = partialTransaction;
            this.proposalId = proposalId;
        }

        public ProposedTransaction(Transaction.SigHash hashType,
                                   boolean anyoneCanPay,
                                   Transaction partialTransaction) {
            this(hashType, anyoneCanPay, partialTransaction, generateRandomProposalId());
        }

        private static UUID generateRandomProposalId() {
            return UUID.randomUUID();
        }

        public Transaction.SigHash getHashType() {
            return hashType;
        }

        public boolean isAnyoneCanPay() {
            return anyoneCanPay;
        }

        public Transaction getPartialTransaction() {
            return partialTransaction;
        }

        public UUID getProposalId() {
            return proposalId;
        }
    }

    /**
     * Request a signature on a transaction
     *
     * @return whether the signer was able to handle this request
     */
    boolean requestSignature(ProposedTransaction proposal) throws IOException;

    void cancelRequest(ProposedTransaction proposal) throws IOException;

    void addListener(RequestListener listener);

    /**
     * Transaction state listener
     */
    public interface RequestListener {
        void requestComplete(UUID requestId, Transaction transaction);
    }
}
