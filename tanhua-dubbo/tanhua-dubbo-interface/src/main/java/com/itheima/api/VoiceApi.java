package com.itheima.api;

import com.itheima.mongo.Voice;

import java.util.List;

public interface VoiceApi {
    String save(Voice voice);

    List<Voice> findByGenderAndCreated(String gender);
}
