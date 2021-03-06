package xyz.n7mn.dev;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import okhttp3.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class WorldUploader {

    public static void main(String[] args) {

        try {

            System.out.println("Minecraft IDを入力してEnterを押してください。\n(例: 7mi_chan)");
            Scanner scanner1 = new Scanner(System.in);
            String minecraftID = scanner1.next();

            System.out.println("スタート位置を入力してください。なければそのままEnterを押してください。\n(例: 0 100 0)");
            Scanner scanner2 = new Scanner(System.in);
            String temp = scanner2.nextLine();
            String d = new String(Base64.getEncoder().encode(temp.getBytes(StandardCharsets.UTF_8)));


            System.out.println("ワールドのフォルダがあるsavesフォルダを指定してください。\n(例：C:\\user\\AppData\\Roaming\\.minecraft\\saves)");
            Scanner scanner3 = new Scanner(System.in);
            String pass = scanner3.next();

            File file = new File(pass);
            if (!file.exists()) {
                System.out.println("存在しないようです...？");
                return;
            }

            File[] fileList = file.listFiles();

            System.out.println("アップロードしたいワールドの番号を入力してください。\n(以下にアップロードしたいフォルダがない場合は先程の指定が間違っている可能性があります。最初からやり直してください。)");
            int i = 0;
            for (File file1 : fileList) {
                if (file1.getName().equals(".")) {
                    i++;
                    continue;
                }
                if (file1.getName().equals("..")) {
                    i++;
                    continue;
                }

                if (file1.isDirectory()) {
                    System.out.println(i + " : " + file1.getName());
                }
                i++;
            }

            Scanner scanner4 = new Scanner(System.in);
            int num = scanner4.nextInt();

            System.out.println("ワールド選択完了 zip化します...");
            ZipParameters params = new ZipParameters();
            params.setCompressionMethod(CompressionMethod.DEFLATE);
            params.setCompressionLevel(CompressionLevel.NORMAL);

            ZipFile zip = new ZipFile("./temp.zip");
            try {
                zip.addFolder(fileList[num]);
            } catch (ZipException e) {
                System.out.println("エラーが発生しました。\n以下のエラーメッセージを報告するか最初からやり直してください。");
                e.printStackTrace();
                new File("./temp.zip").deleteOnExit();
                return;
            }

            System.out.println("zip化完了 アップロードします...");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://n7mn.xyz/upload.php?id="+minecraftID+"&d="+d)
                    .post(RequestBody.create(new File("./temp.zip"), MediaType.parse("application/zip")))
                    .build();

            Response response = client.newCall(request).execute();
            // System.out.println(response.body().string());
            response.close();
            new File("./temp.zip").delete();
            System.out.println("アップロード完了しました。終了しても大丈夫です。");

        } catch (Exception e) {
            System.out.println("エラーが発生しました。\n以下のエラーメッセージを報告するか最初からやり直してください。");
            e.printStackTrace();
        }
    }
}
