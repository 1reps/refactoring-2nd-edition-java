package me.chapter01.asis.data;

import java.util.List;

public record Invoice(String customer, List<Performance> performances) {

}
