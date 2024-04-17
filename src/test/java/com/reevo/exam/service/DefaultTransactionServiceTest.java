package com.reevo.exam.service;

import com.reevo.exam.data.TransactionEntity;
import com.reevo.exam.data.TransactionRepository;
import com.reevo.exam.exception.TransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultTransactionServiceTest {

  private TransactionRepository repo;

  private DefaultTransactionService service;

  @BeforeEach
  void setup() {
    repo = mock(TransactionRepository.class);
    service = new DefaultTransactionService(repo);
  }

  @AfterEach
  void cleanup() {
    reset(repo);
  }

  @Test
  void startShouldSucceed() {
    // given
    when(repo.save(any())).thenAnswer(inv -> inv.getArguments()[0]);

    // when
    var txId = service.start();

    // then
    assertNotNull(txId);
    verify(repo, times(1)).save(argThat(ent -> ent.getId().equals(txId)));
  }

  @Test
  void commitFailsWhenTxNotFound() {
    // given
    var txId = UUID.randomUUID();
    when(repo.findById(txId)).thenReturn(Optional.empty());

    // when
    try {
      service.commit(txId, false);
      fail("should not reach this line.");
    }
    catch (Exception ex) {
      assertTrue(ex instanceof TransactionException);
      verify(repo, times(0)).save(any());
    }
  }

  @Test
  void commitFailsWhenParamFailIsTrue() {
    // given
    var tx = new TransactionEntity();
    when(repo.findById(tx.getId())).thenReturn(Optional.of(tx));

    // when
    try {
      service.commit(tx.getId(), true);
      fail("should not reach this line.");
    }
    catch (Exception ex) {
      assertTrue(ex instanceof TransactionException);
      verify(repo, times(0)).save(any());
    }
  }

  @Test
  void commitSucceeds() {
    // given
    var tx = new TransactionEntity();
    when(repo.findById(tx.getId())).thenReturn(Optional.of(tx));

    // when
    service.commit(tx.getId(), false);

    // then
    assertEquals(TransactionEntity.STATE_COMMITTED, tx.getState());
    assertNotEquals(tx.getCreatedOn(), tx.getLastModifiedOn());
    assertTrue(tx.getCreatedOn().isBefore(tx.getLastModifiedOn()));
    verify(repo, times(1)).save(any());
  }

  @Test
  void rollbackFailsWhenTxNotFound() {
    // given
    var txId = UUID.randomUUID();
    when(repo.findById(txId)).thenReturn(Optional.empty());

    // when
    try {
      service.rollback(txId, false);
      fail("should not reach this line.");
    }
    catch (Exception ex) {
      assertTrue(ex instanceof TransactionException);
      verify(repo, times(0)).save(any());
    }
  }

  @Test
  void rollbackFailsWhenParamFailIsTrue() {
    // given
    var tx = new TransactionEntity();
    when(repo.findById(tx.getId())).thenReturn(Optional.of(tx));

    // when
    try {
      service.rollback(tx.getId(), true);
      fail("should not reach this line.");
    }
    catch (Exception ex) {
      assertTrue(ex instanceof TransactionException);
      verify(repo, times(0)).save(any());
    }
  }

  @Test
  void rollbackSucceeds() {
    // given
    var tx = new TransactionEntity();
    when(repo.findById(tx.getId())).thenReturn(Optional.of(tx));

    // when
    service.rollback(tx.getId(), false);

    // then
    assertEquals(TransactionEntity.STATE_ROLLEDBACK, tx.getState());
    assertNotEquals(tx.getCreatedOn(), tx.getLastModifiedOn());
    assertTrue(tx.getCreatedOn().isBefore(tx.getLastModifiedOn()));
    verify(repo, times(1)).save(any());
  }
}