package com.study.userservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class CacheStartupClearer {

  @Autowired private CacheManager cacheManager;

  /** Clear Redis cash on start up of the application */
  @EventListener
  public void onApplicationReady(ApplicationReadyEvent event) {
    // not working - do not collecting names:
    // cacheManager.getCacheNames().parallelStream().forEach(n ->
    //      cacheManager.getCache(n).clear());
    cacheManager.getCache("users").clear();
    cacheManager.getCache("cards").clear();
    cacheManager.getCache("all").clear();
    cacheManager.getCache("allcards").clear();
  }
}
