public class Neighbor {
    private String ip;
    private boolean access;
    private boolean reply;
    private boolean ok;

    public Neighbor(String ip) {
        this.ip = ip;
        this.access = false;
        this.reply = false;
        this.ok = false;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
