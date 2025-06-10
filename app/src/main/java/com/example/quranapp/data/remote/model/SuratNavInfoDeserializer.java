package com.example.quranapp.data.remote.model;

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
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            SuratNavInfo navInfo = new SuratNavInfo();

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
            return navInfo;

        } else if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean() && !primitive.getAsBoolean()) {
                return null;
            }
        }
        return null;
    }
}
