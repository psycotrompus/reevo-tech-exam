package com.reevo.exam.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class TransactionEntity {

  public static final int STATE_STARTED = 0;

  public static final int STATE_COMMITTED = 1;

  public static final int STATE_ROLLEDBACK = 2;

  @Id
  private UUID id = UUID.randomUUID();

  @Column(nullable = false)
  private int state = STATE_STARTED;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createdOn = LocalDateTime.now();

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime lastModifiedOn = LocalDateTime.now();

  public void commit() {
    state = STATE_COMMITTED;
    lastModifiedOn = LocalDateTime.now();
  }

  public void rollback() {
    state = STATE_ROLLEDBACK;
    lastModifiedOn = LocalDateTime.now();
  }
}
