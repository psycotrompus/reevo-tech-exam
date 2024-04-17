package com.reevo.exam.service;

import com.reevo.exam.exception.TransactionException;

import java.util.UUID;

public interface TransactionService {

  UUID start();

  void commit(UUID txId, boolean fail) throws TransactionException;

  void rollback(UUID txId, boolean fail) throws TransactionException;
}
