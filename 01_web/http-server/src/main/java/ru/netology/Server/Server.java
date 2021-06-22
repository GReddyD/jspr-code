package ru.netology.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class Server {
	private static final int SERVER_PORT = 44444;
	public static final String GET = "GET";
	public static final String POST = "POST";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy @ HH:mm:ss", Locale.US);
	public static String date = formatter.format(LocalDateTime.now());



	protected void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("Запуск сервера");
			upServer(serverSocket);
			System.out.println("Сервер запущен");
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	private void upServer(ServerSocket serverSocket) {
//		final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
//						"/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
			new Thread(() -> {
			final var allowedMethods = List.of(GET, POST);
			while (true) {
				try (
								final var socket = serverSocket.accept();
								final var in = new BufferedInputStream(socket.getInputStream());
								final var out = new BufferedOutputStream(socket.getOutputStream());
				) {
					// лимит на request line + заголовки
					final var limit = 4096;

					in.mark(limit);
					final var buffer = new byte[limit];
					final var read = in.read(buffer);

					// ищем request line
					final var requestLineDelimiter = new byte[]{'\r', '\n'};
					final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
					if (requestLineEnd == -1) {
						badRequest(out);
						continue;
					}

					// читаем request line
					final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
					if (requestLine.length != 3) {
						badRequest(out);
						continue;
					}

					final var method = requestLine[0];
					if (!allowedMethods.contains(method)) {
						badRequest(out);
						continue;
					}

					final var path = requestLine[1];
					if (!path.startsWith("/")) {
						badRequest(out);
						continue;
					}

					// Парсим параметры в URL
					getQueryParams(path, method, StandardCharsets.UTF_8);
					goodRequest(out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static void goodRequest(BufferedOutputStream out) throws IOException {
		out.write((
						"HTTP/1.1 200 OK\r\n" +
													"Content-Length: 0\r\n" +
													"Connection: close\r\n" +
													"\r\n"
		).getBytes());
		out.flush();
	}

	private static void badRequest(BufferedOutputStream out) throws IOException {
		out.write((
						"HTTP/1.1 400 Bad Request\r\n" +
										"Content-Length: 0\r\n" +
										"Connection: close\r\n" +
										"\r\n"
		).getBytes());
		out.flush();
	}

	private static int indexOf(byte[] array, byte[] target, int start, int max) {
		outer:
		for (int i = start; i < max - target.length + 1; i++) {
			for (int j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	public static void getQueryParams(String parameters, String method, Charset charset) {
		try {
			List<NameValuePair> result = URLEncodedUtils.parse(new URI(parameters), String.valueOf(charset));
			System.out.println("[" + date + "]: " + method);
			System.out.println("request path: " + parameters);
			for (NameValuePair nvp : result) {
				System.out.println(nvp);
			}
			System.out.println();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
