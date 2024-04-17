package com.reevo.exam.service;

import com.reevo.exam.data.TransactionEntity;
import com.reevo.exam.data.TransactionRepository;
import com.reevo.exam.exception.TransactionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
@RequiredArgsConstructor
class DefaultTransactionService implements TransactionService {

  private final TransactionRepository txRepo;

  @Override
  public UUID start() {
    var tx = new TransactionEntity();
    txRepo.save(tx);
    return tx.getId();
  }

  @Override
  public void commit(UUID txId, boolean fail) throws TransactionException {
    var tx = txRepo.findById(txId).orElseThrow(() -> new TransactionException("Invalid transaction: " + txId));
    try {
      TimeUnit.SECONDS.sleep(3);
    }
    catch (InterruptedException e) {
      throw new TransactionException("Commit has failed.");
    }
    if (fail) {
      throw new TransactionException("Commit has failed.");
    }
    tx.commit();
    txRepo.save(tx);
  }

  @Override
  public void rollback(UUID txId, boolean fail) throws TransactionException {
    var tx = txRepo.findById(txId).orElseThrow(() -> new TransactionException("Invalid transaction: " + txId));
    try {
      TimeUnit.SECONDS.sleep(3);
    }
    catch (InterruptedException ex) {
      throw new TransactionException("Rollback has failed.");
    }
    if (fail) {
      throw new TransactionException("Rollback has failed.");
    }
    tx.rollback();
    txRepo.save(tx);
  }
}
