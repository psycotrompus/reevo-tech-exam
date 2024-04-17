package com.reevo.exam.rest;

import com.reevo.exam.exception.TransactionException;
import com.reevo.exam.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService txSvc;

  @PostMapping("/start-transaction")
  public ResponseEntity<String> start() {
    var id = txSvc.start();
    return ResponseEntity.ok(id.toString());
  }

  @PostMapping("/commit-transaction/{txId}")
  public ResponseEntity<String> commit(@PathVariable String txId, HttpServletRequest req) {
    txSvc.commit(UUID.fromString(txId), req.getParameterMap().containsKey("fail"));
    return ResponseEntity.ok("Transaction committed: " + txId);
  }

  @PostMapping("/rollback-transaction/{txId}")
  public ResponseEntity<String> rollback(@PathVariable String txId, HttpServletRequest req) {
    txSvc.rollback(UUID.fromString(txId), req.getParameterMap().containsKey("fail"));
    return ResponseEntity.ok("Transaction rolled back: " + txId);
  }

  @ExceptionHandler
  public ResponseEntity<String> handleTransactionException(TransactionException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }
}
