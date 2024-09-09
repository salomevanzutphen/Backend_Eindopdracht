package nl.novi.LivingInSync.utils;

import nl.novi.LivingInSync.model.ImageData;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@Component
public class ImageUtil {


     //Dit is een compression om de foto klein te maken
    public static byte[] compressImage(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte [ 4 * 1024];

        try{
            while (!deflater.finished()){
                int size = deflater.deflate(tmp);
                outputStream.write(tmp, 0 , size);
            }
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
   }

   // Dit is een decompression om de foto weer groot te maken
    public static byte[] decompressImage(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];
        try{
            while(!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
            } catch (DataFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();

            }


}



