import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SenkronHavayoluRezervasyonSistemi {
    private static final Map<Integer, Boolean> database = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        // İlk başta tüm koltuklar boş (false) olarak işaretlenir
        for (int i = 1; i <= 5; i++) {
            database.put(i, false);
        }
    }

    public void makeReservation(Integer seat) {
        lock.writeLock().lock();
        try {
            logCurrentTime();
            log(Thread.currentThread().getName() + " koltuk " + seat + " için rezervasyon yapmaya çalışıyor.");

            if (isSeatAvailable(seat)) {
                reserveSeat(seat);
                log(Thread.currentThread().getName() + " koltuk numarası " + seat + " başarıyla rezerve edildi.");
            } else {
                log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonu yapılamadı, zaten rezerve edilmiş.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void cancelReservation(Integer seat) {
        lock.writeLock().lock();
        try {
            logCurrentTime();
            if (isSeatReserved(seat)) { // isSeatReserved methodunu çağırıyoruz
                log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonunu iptal ediyor.");
                unreserveSeat(seat);
                log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonunu iptal etti.");
            } else {
                log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonu iptal edilemedi, zaten rezerve edilmemiş.");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void queryReservation() {
        lock.readLock().lock();
        try {
            logCurrentTime();
            log(Thread.currentThread().getName() + " mevcut koltuk durumunu sorguluyor. Koltukların durumu: ");
            log(getCurrentSeatStatus());
        } finally {
            lock.readLock().unlock();
        }
    }

    private String getCurrentSeatStatus() {
        StringBuilder result = new StringBuilder();
        database.forEach((key, value) -> {
            String status = value ? "1" : "0";
            result.append("Koltuk No ").append(key).append(" : ").append(status).append(" ");
        });
        return result.toString().trim();
    }

    private boolean isSeatAvailable(Integer seat) {
        return database.get(seat) != null && !database.get(seat);
    }
    
    private boolean isSeatReserved(Integer seat) {
        return database.get(seat) != null && database.get(seat);
    }

    private void reserveSeat(Integer seat) {
        database.put(seat, true);
    }
    
    private void unreserveSeat(Integer seat) {
        database.put(seat, false);
    }

    private void logCurrentTime() {
        log("Zaman: " + getCurrentTime());
    }

    private String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        return currentDateTime.format(formatter);
    }

    private void log(String message) {
        System.out.println(message);
        System.out.println();
    }

    public static void main(String[] args) {
        SenkronHavayoluRezervasyonSistemi system = new SenkronHavayoluRezervasyonSistemi();

        Thread[] threads = {
            new Thread(() -> system.makeReservation(1), "Yazıcı1"),
            new Thread(() -> system.makeReservation(1), "Yazıcı2"),
            new Thread(() -> system.queryReservation(), "Okuyucu1"),
            new Thread(() -> system.queryReservation(), "Okuyucu2"),
            new Thread(() -> system.queryReservation(), "Okuyucu3"),
            new Thread(() -> system.makeReservation(1), "Yazıcı3"),
            new Thread(() -> system.makeReservation(1), "Yazıcı4"),
            new Thread(() -> system.cancelReservation(1), "Yazıcı1")
        };

        for (Thread thread : threads) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

