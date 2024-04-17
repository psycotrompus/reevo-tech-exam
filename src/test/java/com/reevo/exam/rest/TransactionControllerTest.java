package com.reevo.exam.rest;

import com.reevo.exam.exception.TransactionException;
import com.reevo.exam.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @MockBean
  private TransactionService service;

  @Autowired
  private MockMvc mvc;

  @AfterEach
  void cleanup() {
    reset(service);
  }

  @Test
  void shouldStartTransaction() throws Exception {
    // given
    var txId = UUID.randomUUID();
    when(service.start()).thenReturn(txId);

    // when
    var res = mvc.perform(post("/start-transaction")).andDo(print());

    // then
    var body = res.andExpect(status().isOk()).andReturn();
    try {
      UUID.fromString(body.getResponse().getContentAsString());
    }
    catch (Exception ex) {
      fail("response body is invalid.");
    }
  }

  @Test
  void commitHasFailed() throws Exception {
    // given
    var txId = UUID.randomUUID();
    var message = "some random message";
    doThrow(new TransactionException(message)).when(service).commit(txId, false);

    // when
    var res = mvc.perform(post("/commit-transaction/" + txId)).andDo(print());

    // then
    var body = res.andExpect(status().isBadRequest()).andReturn();
    assertEquals(message, body.getResponse().getContentAsString());
  }

  @Test
  void commitSucceeds() throws Exception {
    // given
    var txId = UUID.randomUUID();
    doNothing().when(service).commit(eq(txId), anyBoolean());

    // when
    var res = mvc.perform(post("/commit-transaction/" + txId)).andDo(print());

    // then
    var body = res.andExpect(status().isOk()).andReturn();
    assertEquals("Transaction committed: " + txId, body.getResponse().getContentAsString());
  }

  @Test
  void rollbackHasFailed() throws Exception {
    // given
    var txId = UUID.randomUUID();
    var message = "some random message";
    doThrow(new TransactionException(message)).when(service).rollback(txId, false);

    // when
    var res = mvc.perform(post("/rollback-transaction/" + txId)).andDo(print());

    // then
    var body = res.andExpect(status().isBadRequest()).andReturn();
    assertEquals(message, body.getResponse().getContentAsString());
  }

  @Test
  void rollbackSucceeds() throws Exception {
    // given
    var txId = UUID.randomUUID();
    doNothing().when(service).rollback(eq(txId), anyBoolean());

    // when
    var res = mvc.perform(post("/rollback-transaction/" + txId)).andDo(print());

    // then
    var body = res.andExpect(status().isOk()).andReturn();
    assertEquals("Transaction rolled back: " + txId, body.getResponse().getContentAsString());
  }

}