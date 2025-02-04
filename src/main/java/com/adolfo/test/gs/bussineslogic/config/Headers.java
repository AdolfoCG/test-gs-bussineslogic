package com.adolfo.test.gs.bussineslogic.config;

import lombok.Getter;

@Getter
public class Headers {
  private final String name;
  private final String value;

  public Headers(String name, String value) {
    this.name = name;
    this.value = value;
  }

}