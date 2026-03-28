package com.ocean.tigaapi.hint.controller;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import com.ocean.tigaapi.hint.service.HintScanner;

/**
 * 提供给前端 Monaco 使用的元数据接口
 */
@Controller
@Mapping("/tiga/meta")
public class HintController {

    @Inject
    private HintScanner tigaMetadataScanner;

    /**
     * 获取全量压缩类名（用于初次加载提示）
     *    返回内容已在服务中缓存，这里仅做输出
     *    同时使用 gzip 压缩，减少体积
     */
    @Get
    @Mapping("/classes.txt")
    public void classesText(Context ctx) throws Exception {
        String text = tigaMetadataScanner.getCompressedText(); // 已缓存
        byte[] raw = text.getBytes(StandardCharsets.UTF_8);

        // gzip 压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(raw);
        }
        byte[] gzipped = baos.toByteArray();

        // 设置响应头，让浏览器自动解压
        ctx.headerSet("Content-Type", "text/plain; charset=utf-8");
        ctx.headerSet("Content-Encoding", "gzip");
        ctx.output(gzipped);
    }
    /**
     * 获取单个类的详细方法（点号联想时调用）
     */
    @Post
    @Mapping("/class")
    public Result<Object> loadClass(String className) {
        try {
            return Result.succeed(tigaMetadataScanner.getScriptClass(className));
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
}
