package server;

import commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nickname;
    private String login;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    socket.setSoTimeout(12000);
                    // цикл аутентификации
                    while (true) {
                        String str = in.readUTF();
                        System.out.println("ClientHandler in.readUTF() = " + str);

                        // если команда отключиться
                        if (str.equals(Command.END)) {
                            out.writeUTF(Command.END);
                            throw new RuntimeException("Client wants to disconnect");
                        }

                        // если команда аутентификация
                        if (str.startsWith(Command.AUTH)) {
                            String[] token = str.split("\\s");
                            if (token.length < 3) {
                                continue;
                            }
                            String newNick = server.getDatabaseUsers().getNickname(token[1], token[2]);
                            login = token[1];
                            System.out.println("ClientHandler login=" + login);

                            if (newNick != null) {
                                System.out.println("newNick != null");
                                if (!server.isLoginAuthenticated(login)) {
                                    nickname = newNick;
                                    sendMsg(Command.AUTH_OK + " " + nickname);
                                    server.subscribe(this);
                                    System.out.println("client: " +
                                            socket.getRemoteSocketAddress() + " connected with nick: " + nickname);
                                    socket.setSoTimeout(0);
                                    break;
                                } else {
                                    sendMsg("login is using");
                                }
                            } else {
                                sendMsg("wrong login/password");
                            }
                        }

                        // если команда регистрация
                        if (str.startsWith(Command.REG)) {
                            String[] token = str.split("\\s", 4);
                            if (token.length < 4) {
                                continue;
                            }
                            boolean regSuccess = server.getDatabaseUsers().insertNewUserPS(token[1], token[2], token[3]);
                            if (regSuccess) {
                                sendMsg(Command.REG_OK);
                            } else {
                                sendMsg(Command.REG_NON);
                            }
                        }
                    }

                    // цикл работы
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals(Command.END)) {
                                out.writeUTF(Command.END);
                                break;
                            }

                            if (str.startsWith(Command.PRIVATE_MSG)) {
                                String[] token = str.split("\\s", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);

                            }
                        } else {
                            server.broadcastMsg(this, str);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } catch (SocketTimeoutException e) {
                    try {
                        out.writeUTF(Command.END);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Client disconnected: " + nickname);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
