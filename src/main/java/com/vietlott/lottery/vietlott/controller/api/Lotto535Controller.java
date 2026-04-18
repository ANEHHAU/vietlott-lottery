package com.vietlott.lottery.vietlott.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.SecureRandom;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/lotto-535")
public class Lotto535Controller {

    private static final SecureRandom secureRandom = new SecureRandom(UUID.randomUUID().toString().getBytes());
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final int MAX_ROUNDS = 200000;     // Tăng để có cơ hội thỏa mãn điều kiện khắt khe hơn
    private static final int MAIN_POOL = 35;
    private static final int SPECIAL_POOL = 12;

    public List<Integer> generateNumbers() {
        List<Integer> pool = new ArrayList<>();
        for (int i = 1; i <= MAIN_POOL; i++) pool.add(i);
        Collections.shuffle(pool, secureRandom);
        List<Integer> result = new ArrayList<>(pool.subList(0, 5));
        Collections.sort(result);
        int special = secureRandom.nextInt(SPECIAL_POOL) + 1;
        result.add(special);
        return result;
    }

    @GetMapping(value = "/predict", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter predict() {
        SseEmitter emitter = new SseEmitter(0L);
        executor.submit(() -> {
            try {
                System.out.println("[SSE 535] Bắt đầu kết nối SSE");
                emitter.send(SseEmitter.event().name("start").data("Bắt đầu mô phỏng AI..."));

                Map<String, Object> response = runSimulation(emitter);

                emitter.send(SseEmitter.event().name("complete").data(response));
                emitter.complete();
                System.out.println("[SSE 535] Hoàn thành thành công");
            } catch (Exception e) {
                System.err.println("[SSE 535] Lỗi: " + e.getMessage());
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    private Map<String, Object> runSimulation(SseEmitter emitter) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> rounds = new ArrayList<>();

        Map<Integer, Map<Integer, Integer>> freqAll = new HashMap<>();
        for (int i = 0; i < 6; i++) freqAll.put(i, new HashMap<>());

        System.out.println("[SSE 535] Bắt đầu phase 1 - Chờ MỖI vị trí có ĐÚNG 1 số duy nhất ít xuất hiện nhất (>0) VÀ 5 số đó khác nhau");

        int round = 0;
        boolean conditionMet = false;

        while (true) {
            round++;

            if (round > MAX_ROUNDS) {
                System.out.println("[SSE 535] Đạt giới hạn " + MAX_ROUNDS + " vòng → Dừng và dùng best effort (số ít xuất hiện nhất hiện tại)");
                break;
            }

            List<Integer> numbers = generateNumbers();

            if (round % 1000 == 0 || round <= 5) {
                System.out.printf("[SSE 535] Vòng %d (đang chờ unique min >0 & distinct)%n", round);
            }

            Map<String, Object> roundData = new HashMap<>();
            roundData.put("round", round);
            roundData.put("numbers", numbers);
            rounds.add(roundData);

            // Cập nhật tần suất
            for (int i = 0; i < 6; i++) {
                int num = numbers.get(i);
                Map<Integer, Integer> freq = freqAll.get(i);
                freq.put(num, freq.getOrDefault(num, 0) + 1);
            }

            // ================= KIỂM TRA ĐIỀU KIỆN CHÍNH XÁC =================
            boolean eachPosHasUniqueMinGT0 = true;
            Integer[] minNumberAtPos = new Integer[6]; // lưu số có tần suất min duy nhất tại mỗi vị trí

            for (int pos = 0; pos < 6; pos++) {
                Map<Integer, Integer> freq = freqAll.get(pos);
                int maxNum = (pos < 5) ? MAIN_POOL : SPECIAL_POOL;

                int minCount = Integer.MAX_VALUE;
                int numWithMin = 0;
                int theNumber = -1;

                for (int n = 1; n <= maxNum; n++) {
                    int f = freq.getOrDefault(n, 0);
                    if (f < minCount) {
                        minCount = f;
                        numWithMin = 1;
                        theNumber = n;
                    } else if (f == minCount) {
                        numWithMin++;
                    }
                }

                if (numWithMin != 1 || minCount <= 0) {
                    eachPosHasUniqueMinGT0 = false;
                    break;
                }
                minNumberAtPos[pos] = theNumber;
            }

            // Nếu mỗi vị trí đã có unique min >0, kiểm tra tiếp 5 số đầu có khác nhau không
            boolean distinctMain = false;
            if (eachPosHasUniqueMinGT0) {
                Set<Integer> mainCandidates = new HashSet<>();
                for (int pos = 0; pos < 5; pos++) {
                    mainCandidates.add(minNumberAtPos[pos]);
                }
                distinctMain = (mainCandidates.size() == 5);
            }

            conditionMet = eachPosHasUniqueMinGT0 && distinctMain;
            // ============================================================

            if (round % 500 == 0 || conditionMet) {
                int percent = Math.min(100, (int) (round * 100.0 / MAX_ROUNDS));
                emitter.send(SseEmitter.event().name("progress").data(percent));
            }
            if (round % 30 == 0) {
                emitter.send(SseEmitter.event().name("ping").data("ok"));
            }
            if (round % 2000 == 0 || round <= 10 || conditionMet) {
                emitter.send(SseEmitter.event().name("round").data(roundData));
            }

            if (conditionMet) {
                System.out.printf("[SSE 535] ĐÃ THỎA MÃN ĐIỀU KIỆN (mỗi vị trí unique min >0 và 5 số chính phân biệt) tại vòng %d%n", round);
                emitter.send(SseEmitter.event().name("progress").data(100));
                break;
            }
        }

        System.out.println("[SSE 535] Phase 1 xong → Phase 2 (chọn số dựa trên tần suất)");

        Integer[] mainResult = new Integer[5];
        Set<Integer> used = new HashSet<>();

        if (conditionMet) {
            // Khi đã đủ điều kiện, các số min duy nhất tại 5 vị trí đầu đã khác nhau
            // Lấy trực tiếp chúng làm kết quả chính
            for (int pos = 0; pos < 5; pos++) {
                Map<Integer, Integer> freq = freqAll.get(pos);
                int minCount = Collections.min(freq.values());
                for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                    if (e.getValue() == minCount) {
                        mainResult[pos] = e.getKey();
                        break;
                    }
                }
            }
            // Sắp xếp lại cho đẹp (vẫn giữ đúng bộ số)
            List<Integer> mainList = new ArrayList<>(Arrays.asList(mainResult));
            Collections.sort(mainList);
            mainResult = mainList.toArray(new Integer[0]);

            // Số đặc biệt: lấy số có tần suất min duy nhất tại vị trí 5
            Map<Integer, Integer> specialFreq = freqAll.get(5);
            int specialMin = Collections.min(specialFreq.values());
            int specialResult = -1;
            for (Map.Entry<Integer, Integer> e : specialFreq.entrySet()) {
                if (e.getValue() == specialMin) {
                    specialResult = e.getKey();
                    break;
                }
            }
            List<Integer> mainFinal = Arrays.asList(mainResult);
            System.out.println("[SSE 535] Kết quả cuối (đúng điều kiện): Số chính " + mainFinal + " | Đặc biệt " + specialResult);
            response.put("main", mainFinal);
            response.put("special", specialResult);
            response.put("note", "Đã tìm được bộ số thoả mãn: mỗi vị trí có đúng 1 số ít xuất hiện nhất (>0) và 5 số chính khác nhau.");
        } else {
            // Best effort: dùng số có tần suất nhỏ nhất, ưu tiên chưa dùng, không bao giờ lấy số nhiều nhất
            for (int i = 0; i < 5; i++) {
                Map<Integer, Integer> freq = freqAll.get(i);
                for (int n = 1; n <= MAIN_POOL; n++) freq.putIfAbsent(n, 0);

                int minCount = Collections.min(freq.values());
                List<Integer> candidates = new ArrayList<>();
                for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                    if (e.getValue() == minCount) candidates.add(e.getKey());
                }
                Collections.sort(candidates);

                boolean assigned = false;
                for (int num : candidates) {
                    if (!used.contains(num)) {
                        mainResult[i] = num;
                        used.add(num);
                        assigned = true;
                        break;
                    }
                }
                if (!assigned) {
                    List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(freq.entrySet());
                    sorted.sort(Comparator.comparingInt(Map.Entry::getValue));
                    for (Map.Entry<Integer, Integer> e : sorted) {
                        if (!used.contains(e.getKey())) {
                            mainResult[i] = e.getKey();
                            used.add(e.getKey());
                            break;
                        }
                    }
                }
            }

            List<Integer> mainList = new ArrayList<>(Arrays.asList(mainResult));
            Collections.sort(mainList);

            Map<Integer, Integer> specialFreq = freqAll.get(5);
            for (int n = 1; n <= SPECIAL_POOL; n++) specialFreq.putIfAbsent(n, 0);
            int specialMinCount = Collections.min(specialFreq.values());
            List<Integer> specialCandidates = new ArrayList<>();
            for (Map.Entry<Integer, Integer> e : specialFreq.entrySet()) {
                if (e.getValue() == specialMinCount) specialCandidates.add(e.getKey());
            }
            Collections.sort(specialCandidates);
            int specialResult = specialCandidates.get(0);

            System.out.println("[SSE 535] Kết quả cuối (best effort): Số chính " + mainList + " | Đặc biệt " + specialResult);
            response.put("main", mainList);
            response.put("special", specialResult);
            response.put("note", "Đạt giới hạn vòng - Dùng số ít xuất hiện nhất hiện tại (có thể còn vài số freq=0 hoặc trùng min giữa các vị trí).");
        }

        response.put("freq", freqAll);
        response.put("rounds", rounds);
        response.put("conditionFullyMet", conditionMet);
        response.put("totalRounds", round);

        return response;
    }
}