package ru.netology.Server;

import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    Server server = new Server(64);
    server.listen();
  }
}


