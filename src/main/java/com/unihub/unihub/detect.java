package com.unihub.unihub;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class detect {
    private static final String VOCABULARY_DIR = "Sensitive_Vocabulary";
    private static final TrieNode root = new TrieNode();
    private static boolean built = false;

    // Trie节点定义
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail = null;
        boolean isEnd = false;
        String word = null;
    }

    // 构建Trie树和fail指针（AC自动机）
    private static void buildTrieAndAC() {
        if (built) return;
        try {
            Path dir = Paths.get(VOCABULARY_DIR);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) return;
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt");
            for (Path file : stream) {
                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        insert(word);
                    }
                }
            }
            buildFailPointers();
            built = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 插入单个敏感词到Trie树
    private static void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.word = word;
    }

    // 构建fail指针
    private static void buildFailPointers() {
        Queue<TrieNode> queue = new LinkedList<>();
        root.fail = root;
        queue.add(root);
        while (!queue.isEmpty()) {
            TrieNode curr = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : curr.children.entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();
                TrieNode failNode = curr.fail;
                while (failNode != root && !failNode.children.containsKey(c)) {
                    failNode = failNode.fail;
                }
                if (failNode.children.containsKey(c) && failNode.children.get(c) != child) {
                    child.fail = failNode.children.get(c);
                } else {
                    child.fail = root;
                }
                queue.add(child);
            }
        }
    }

    // AC自动机检测文本是否包含敏感词
    public static boolean containsSensitiveWord(String text) {
        buildTrieAndAC();
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            node = node.children.getOrDefault(c, root);
            TrieNode temp = node;
            while (temp != root) {
                if (temp.isEnd) {
                    return true;
                }
                temp = temp.fail;
            }
        }
        return false;
    }

    // AC自动机返回所有命中的敏感词
    public static Set<String> getMatchedWords(String text) {
        buildTrieAndAC();
        Set<String> matched = new HashSet<>();
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }
            node = node.children.getOrDefault(c, root);
            TrieNode temp = node;
            while (temp != root) {
                if (temp.isEnd) {
                    matched.add(temp.word);
                }
                temp = temp.fail;
            }
        }
        return matched;
    }
} 