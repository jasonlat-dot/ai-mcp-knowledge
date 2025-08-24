package com.jasonlat.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileUtils {

    /**
     * 从MD文件中读取提示词内容
     * @param filePath MD文件路径
     * @return 文件内容字符串
     */
    public static String readPromptFromMdFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(filePath).toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 读取失败可根据实际需求处理，比如返回空或抛自定义异常
            return "";
        }
        return content.toString();
    }

    /**
     * 从 classpath 读取文件内容
     * @param classpathPath classpath 路径，如 "prompt/Java面试文章生成提示词.md"
     * @return 文件内容字符串
     */
    public static String readFromClasspath(String classpathPath) throws IOException {

        // 注意：路径不加 classpath:，直接从 resources 下开始写
        ClassPathResource resource = new ClassPathResource(classpathPath);
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(bytes, StandardCharsets.UTF_8);

    }
}
