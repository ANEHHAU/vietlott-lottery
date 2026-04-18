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
@RequestMapping("/api/power-655")
public class Power655Controller {

    private static final SecureRandom secureRandom =
            new SecureRandom(UUID.randomUUID().toString().getBytes());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    // Giới hạn vòng lặp để tránh timeout (đủ để thuật toán ổn định)
    private static final int MAX_ROUNDS = 5000;

    public List<Integer> generateNumbers() {
        List<Integer> pool = new ArrayList<>(55);
        for (int i = 1; i <= 55; i++) pool.add(i);
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
                System.out.println("[SSE] Bắt đầu kết nối SSE");
                emitter.send(SseEmitter.event().name("start").data("Bắt đầu mô phỏng AI..."));

                Map<String, Object> response = getRealAuthNumbersWithLogs(emitter);

                emitter.send(SseEmitter.event().name("complete").data(response));
                emitter.complete();
                System.out.println("[SSE] Hoàn thành, đã gửi complete");

            } catch (Exception e) {
                System.err.println("[SSE] Lỗi: " + e.getMessage());
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private Map<String, Object> getRealAuthNumbersWithLogs(SseEmitter emitter) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> rounds = new ArrayList<>();

        Map<Integer, Map<Integer, Integer>> freqAll = new HashMap<>();
        for (int i = 0; i < 6; i++) freqAll.put(i, new HashMap<>());

        System.out.println("[SSE] Bắt đầu phase 1: thu thập số, max " + MAX_ROUNDS + " vòng");

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            List<Integer> numbers = generateNumbers();

            // Log mỗi 500 vòng
            if (round % 500 == 0 || round <= 3) {
                System.out.printf("[SSE] Vòng %d/%d%n", round, MAX_ROUNDS);
            }

            Map<String, Object> roundData = new HashMap<>();
            roundData.put("round", round);
            roundData.put("numbers", numbers);
            rounds.add(roundData);

            // Gửi round data
            emitter.send(SseEmitter.event().name("round").data(roundData));

            // Cập nhật freq
            for (int i = 0; i < 6; i++) {
                int num = numbers.get(i);
                Map<Integer, Integer> freq = freqAll.get(i);
                freq.put(num, freq.getOrDefault(num, 0) + 1);
            }

            // ✅ FIX % : tính theo số vòng thực tế / max
            int percent = (int) (round * 100.0 / MAX_ROUNDS);
            emitter.send(SseEmitter.event().name("progress").data(percent));

            // Keepalive mỗi 200 vòng để tránh timeout
            if (round % 200 == 0) {
                emitter.send(SseEmitter.event().name("ping").data("ok"));
                System.out.printf("[SSE] Ping keepalive vòng %d%n", round);
            }

            // Check done sớm nếu đủ data (optional, vẫn giữ để nhanh hơn)
            boolean done = freqAll.values().stream().allMatch(f -> f.size() >= 55);
            if (done) {
                System.out.printf("[SSE] Đủ data sớm tại vòng %d, kết thúc sớm%n", round);
                // Gửi 100% khi done sớm
                emitter.send(SseEmitter.event().name("progress").data(100));
                break;
            }
        }

        System.out.println("[SSE] Phase 1 xong, bắt đầu tính kết quả");

        // PHASE 2: chọn kết quả
        Integer[] result = new Integer[6];
        Set<Integer> used = new HashSet<>();

        for (int i = 0; i < 6; i++) {
            Map<Integer, Integer> freq = freqAll.get(i);
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
        }

        System.out.println("[SSE] Kết quả: " + Arrays.toString(result));

        response.put("result", Arrays.asList(result));
        response.put("freq", freqAll);
        response.put("rounds", rounds);
        return response;
    }
}