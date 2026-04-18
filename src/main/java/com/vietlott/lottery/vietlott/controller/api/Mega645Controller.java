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
@RequestMapping("/api/mega-645")
public class Mega645Controller {

    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    // 6 vị trí, mỗi vị trí 1–45 → cần thấy đủ 45 số khác nhau
    private static final int MAX_ROUNDS = 5000;
    private static final int POOL_SIZE = 45;

    // ✅ Giữ nguyên logic random gốc
    public List<Integer> generateNumbers() {
        List<Integer> pool = new ArrayList<>(POOL_SIZE);
        for (int i = 1; i <= POOL_SIZE; i++) pool.add(i);
        Collections.shuffle(pool, secureRandom);
        List<Integer> result = new ArrayList<>(pool.subList(0, 6));
        Collections.sort(result);
        return result;
    }

    @GetMapping(value = "/predict", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter predict() {
        SseEmitter emitter = new SseEmitter(0L);

        executor.submit(() -> {
            try {
                System.out.println("[SSE 645] Bắt đầu kết nối SSE");
                emitter.send(SseEmitter.event().name("start").data("Bắt đầu mô phỏng AI..."));

                Map<String, Object> response = runSimulation(emitter);

                emitter.send(SseEmitter.event().name("complete").data(response));
                emitter.complete();
                System.out.println("[SSE 645] Hoàn thành");

            } catch (Exception e) {
                System.err.println("[SSE 645] Lỗi: " + e.getMessage());
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

        System.out.println("[SSE 645] Bắt đầu phase 1, max " + MAX_ROUNDS + " vòng");

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            List<Integer> numbers = generateNumbers();

            if (round % 500 == 0 || round <= 3) {
                System.out.printf("[SSE 645] Vòng %d/%d%n", round, MAX_ROUNDS);
            }

            Map<String, Object> roundData = new HashMap<>();
            roundData.put("round", round);
            roundData.put("numbers", numbers);
            rounds.add(roundData);

            emitter.send(SseEmitter.event().name("round").data(roundData));

            for (int i = 0; i < 6; i++) {
                int num = numbers.get(i);
                Map<Integer, Integer> freq = freqAll.get(i);
                freq.put(num, freq.getOrDefault(num, 0) + 1);
            }

            int percent = (int) (round * 100.0 / MAX_ROUNDS);
            emitter.send(SseEmitter.event().name("progress").data(percent));

            if (round % 200 == 0) {
                emitter.send(SseEmitter.event().name("ping").data("ok"));
            }

            boolean done = freqAll.values().stream().allMatch(f -> f.size() >= POOL_SIZE);
            if (done) {
                System.out.printf("[SSE 645] Đủ data tại vòng %d%n", round);
                emitter.send(SseEmitter.event().name("progress").data(100));
                break;
            }
        }

        System.out.println("[SSE 645] Phase 1 xong, tính kết quả...");

        // PHASE 2: chọn số ít xuất hiện nhất theo từng vị trí, không trùng
        Integer[] result = new Integer[6];
        Set<Integer> used = new HashSet<>();

        for (int i = 0; i < 6; i++) {
            Map<Integer, Integer> freq = freqAll.get(i);

            // Đảm bảo 1–45 đều có trong freq
            for (int n = 1; n <= POOL_SIZE; n++) freq.putIfAbsent(n, 0);

            int minCount = Collections.min(freq.values());

            List<Integer> candidates = new ArrayList<>();
            for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                if (e.getValue() == minCount) candidates.add(e.getKey());
            }
            Collections.sort(candidates);

            for (int num : candidates) {
                if (!used.contains(num)) {
                    result[i] = num;
                    used.add(num);
                    break;
                }
            }

            // Fallback nếu tất cả candidates đã dùng
            if (result[i] == null) {
                List<Map.Entry<Integer, Integer>> sorted = new ArrayList<>(freq.entrySet());
                sorted.sort(Comparator.comparingInt(Map.Entry::getValue));
                for (Map.Entry<Integer, Integer> e : sorted) {
                    if (!used.contains(e.getKey())) {
                        result[i] = e.getKey();
                        used.add(e.getKey());
                        break;
                    }
                }
            }
        }

        // Sắp xếp kết quả tăng dần cho dễ nhìn
        List<Integer> resultList = new ArrayList<>(Arrays.asList(result));
        Collections.sort(resultList);

        System.out.println("[SSE 645] Kết quả: " + resultList);

        response.put("result", resultList);
        response.put("freq", freqAll);
        response.put("rounds", rounds);
        return response;
    }
}