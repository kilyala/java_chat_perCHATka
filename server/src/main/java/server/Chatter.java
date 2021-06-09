package server;


//это заготовка была, удалять не стал, пока не пригодилась


public class Chatter {
    private final String login;
    private final String password;
    private final String nickname;

    public Chatter(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "Chatter{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
