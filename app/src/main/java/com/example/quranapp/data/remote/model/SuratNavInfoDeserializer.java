package com.example.quranapp.data.remote.model; // Sesuaikan dengan package name aplikasi Anda

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class SuratNavInfoDeserializer implements JsonDeserializer<SuratNavInfo> {

    @Override
    public SuratNavInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Periksa apakah elemen JSON adalah objek
        if (json.isJsonObject()) {
            // Jika ini adalah objek JSON, lanjutkan dengan deserialisasi normal.
            // Kita akan mem-parse field secara manual untuk memastikan integritas.
            JsonObject jsonObject = json.getAsJsonObject();
            SuratNavInfo navInfo = new SuratNavInfo(); // Buat instance baru

            // Ambil nilai dengan aman, periksa keberadaan dan tipe data sebelum mengambilnya
            if (jsonObject.has("nomor") && jsonObject.get("nomor").isJsonPrimitive() && jsonObject.get("nomor").getAsJsonPrimitive().isNumber()) {
                navInfo.setNomor(jsonObject.get("nomor").getAsInt());
            }
            if (jsonObject.has("nama") && jsonObject.get("nama").isJsonPrimitive() && jsonObject.get("nama").getAsJsonPrimitive().isString()) {
                navInfo.setNama(jsonObject.get("nama").getAsString());
            }
            if (jsonObject.has("namaLatin") && jsonObject.get("namaLatin").isJsonPrimitive() && jsonObject.get("namaLatin").getAsJsonPrimitive().isString()) {
                navInfo.setNamaLatin(jsonObject.get("namaLatin").getAsString());
            }
            if (jsonObject.has("jumlahAyat") && jsonObject.get("jumlahAyat").isJsonPrimitive() && jsonObject.get("jumlahAyat").getAsJsonPrimitive().isNumber()) {
                navInfo.setJumlahAyat(jsonObject.get("jumlahAyat").getAsInt());
            }
            return navInfo; // Kembalikan objek SuratNavInfo yang sudah diisi

        } else if (json.isJsonPrimitive()) {
            // Jika elemen JSON adalah tipe primitif
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            // Periksa apakah itu boolean dan nilainya false
            if (primitive.isBoolean() && !primitive.getAsBoolean()) {
                // Jika boolean false, kembalikan null. Ini menandakan tidak ada surat sebelumnya/selanjutnya.
                return null;
            }
        }

        return null;
    }
}
