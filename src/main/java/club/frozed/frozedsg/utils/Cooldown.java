package club.frozed.frozedsg.utils;


import java.text.DecimalFormat;

public class Cooldown
{
    private long start;
    private long expire;
    private static DecimalFormat SECONDS_FORMAT;
    
    public Cooldown(final int seconds) {
        this.start = System.currentTimeMillis();
        final long duration = 1000 * seconds;
        this.expire = this.start + duration;
    }
    
    public long getPassed() {
        return System.currentTimeMillis() - this.start;
    }
    
    public long getRemaining() {
        return this.expire - System.currentTimeMillis();
    }
    
    public boolean hasExpired() {
        return System.currentTimeMillis() - this.expire > 1L;
    }
    
    public int getSecondsLeft() {
        return (int)this.getRemaining() / 1000;
    }
    
    public String getMiliSecondsLeft() {
        return formatSeconds(this.getRemaining());
    }
    
    public String getTimeLeft() {
        return Utils.formatTime(this.getSecondsLeft());
    }
    
    public void cancelCountdown() {
        this.expire = 0L;
    }
    
    private static String formatSeconds(final long time) {
        return Cooldown.SECONDS_FORMAT.format(time / 1000.0f);
    }
    
    public long getStart() {
        return this.start;
    }
    
    public long getExpire() {
        return this.expire;
    }
    
    public void setStart(final long start) {
        this.start = start;
    }
    
    public void setExpire(final long expire) {
        this.expire = expire;
    }
    
    static {
        Cooldown.SECONDS_FORMAT = new DecimalFormat("#0.0");
    }
}
