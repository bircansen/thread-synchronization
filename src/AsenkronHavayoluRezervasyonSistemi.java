import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AsenkronHavayoluRezervasyonSistemi {
    private static final Map<Integer, Boolean> database = new HashMap<>();

    static {
        // İlk başta tüm koltuklar boş (false) olarak işaretlenir
        for (int i = 1; i <= 5; i++) {
            database.put(i, false);
        }
    }
    
    public void makeReservation(Integer seat) {
        log(Thread.currentThread().getName() + " koltuk " + seat + " için rezervasyon yapmaya çalışıyor.");

        // Mevcut işlemi simüle etmek için küçük bir gecikme
        fakeProcessingDelay();

        if (isSeatAvailable(seat)) {
            reserveSeat(seat);
            log(Thread.currentThread().getName() + " koltuk numarası " + seat + " başarıyla rezerve edildi.");
        } else {
            log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonu yapılamadı, zaten rezerve edilmiş.");
        }
    }

    public void cancelReservation(Integer seat) {
        log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonunu iptal ediyor.");

        // Mevcut işlemi simüle etmek için küçük bir gecikme
        fakeProcessingDelay();

        if (isSeatReserved(seat)) {
            unreserveSeat(seat);
            log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonunu iptal etti.");
        } else {
            log(Thread.currentThread().getName() + " koltuk " + seat + " rezervasyonu iptal edilemedi, zaten rezerve edilmemiş.");
        }
    }

    public void queryReservation() {
        log(Thread.currentThread().getName() + " mevcut koltuk durumunu sorguluyor.");

        // Mevcut işlemi simüle etmek için küçük bir gecikme
        fakeProcessingDelay();

        log(getCurrentSeatStatus());
    }

    private String getCurrentSeatStatus() {
        StringBuilder result = new StringBuilder("Koltukların durumu: ");
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

    // Mevcut işlemi simüle etmek için küçük bir gecikme ekleyen yardımcı yöntem
    private void fakeProcessingDelay() {
        try {
            Thread.sleep(100); // 100 milisaniye gecikme eklendi
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void log(String message) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("Zaman: " + currentTime + " - " + message);
    }

    public static void main(String[] args) {
        AsenkronHavayoluRezervasyonSistemi system = new AsenkronHavayoluRezervasyonSistemi();

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
        }
    }
}
