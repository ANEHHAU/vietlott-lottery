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
@RequestMapping("/api/max-3d")
public class Max3DController {

    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final int MAX_ROUNDS = 2000;
    private static final int DIGITS = 10; // 0–9

    // ✅ Giữ nguyên random gốc, chỉ 3 số
    public List<Integer> generateNumbers() {
        List<Integer> result = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            result.add(secureRandom.nextInt(10));
        }
        return result;
    }

    @GetMapping(value = "/predict", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter predict() {
        SseEmitter emitter = new SseEmitter(0L);

        executor.submit(() -> {
            try {
                System.out.println("[SSE 3D] Bắt đầu kết nối SSE");
                emitter.send(SseEmitter.event().name("start").data("Bắt đầu mô phỏng AI..."));

                Map<String, Object> response = runSimulation(emitter);

                emitter.send(SseEmitter.event().name("complete").data(response));
                emitter.complete();
                System.out.println("[SSE 3D] Hoàn thành");

            } catch (Exception e) {
                System.err.println("[SSE 3D] Lỗi: " + e.getMessage());
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

        // freq[vị trí 0-2][chữ số 0-9] = số lần xuất hiện
        Map<Integer, Map<Integer, Integer>> freqAll = new HashMap<>();
        for (int i = 0; i < 3; i++) freqAll.put(i, new HashMap<>());

        System.out.println("[SSE 3D] Bắt đầu phase 1, max " + MAX_ROUNDS + " vòng");

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            List<Integer> numbers = generateNumbers();

            if (round % 200 == 0 || round <= 3) {
                System.out.printf("[SSE 3D] Vòng %d/%d%n", round, MAX_ROUNDS);
            }

            Map<String, Object> roundData = new HashMap<>();
            roundData.put("round", round);
            roundData.put("numbers", numbers);
            rounds.add(roundData);

            emitter.send(SseEmitter.event().name("round").data(roundData));

            // Cập nhật tần suất
            for (int i = 0; i < 3; i++) {
                int digit = numbers.get(i);
                Map<Integer, Integer> freq = freqAll.get(i);
                freq.put(digit, freq.getOrDefault(digit, 0) + 1);
            }

            // % theo vòng
            int percent = (int) (round * 100.0 / MAX_ROUNDS);
            emitter.send(SseEmitter.event().name("progress").data(percent));

            // Keepalive mỗi 200 vòng
            if (round % 200 == 0) {
                emitter.send(SseEmitter.event().name("ping").data("ok"));
            }

            // Kết thúc sớm nếu đủ data
            boolean done = freqAll.values().stream().allMatch(f -> f.size() >= DIGITS);
            if (done) {
                System.out.printf("[SSE 3D] Đủ data tại vòng %d%n", round);
                emitter.send(SseEmitter.event().name("progress").data(100));
                break;
            }
        }

        System.out.println("[SSE 3D] Phase 1 xong, tính kết quả...");

        // PHASE 2: mỗi vị trí chọn chữ số ít xuất hiện nhất
        Integer[] result = new Integer[3];
        Set<Integer> used = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            Map<Integer, Integer> freq = freqAll.get(i);

            // Đảm bảo 0-9 đều có trong freq
            for (int d = 0; d < DIGITS; d++) freq.putIfAbsent(d, 0);

            int minCount = Collections.min(freq.values());

            List<Integer> candidates = new ArrayList<>();
            for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                if (e.getValue() == minCount) candidates.add(e.getKey());
            }
            Collections.sort(candidates);

            for (int digit : candidates) {
                if (!used.contains(digit)) {
                    result[i] = digit;
                    used.add(digit);
                    break;
                }
            }

            // Fallback: nếu tất cả candidates đã dùng
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

        System.out.println("[SSE 3D] Kết quả: " + Arrays.toString(result));

        response.put("result", Arrays.asList(result));
        response.put("freq", freqAll);
        response.put("rounds", rounds);
        return response;
    }
}