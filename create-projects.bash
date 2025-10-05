#!/usr/bin/env bash

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=adoptionpaper-service \
--package-name=com.roos.adoptioncenter.adoptionpaper-service \
--groupId=com.roos.adoptioncenter.adoptionpaper-service \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
adoptionpaper-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=adopter-service \
--package-name=com.roos.adoptioncenter.adopter-service \
--groupId=com.roos.adoptioncenter.adopter-service \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
adopter-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=dogandlocation-service \
--package-name=com.roos.adoptioncenter.dogandlocation-service \
--groupId=com.roos.adoptioncenter.dogandlocation-service \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
dogandlocation-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=volunteer-service \
--package-name=com.roos.adoptioncenter.volunteer-service \
--groupId=com.roos.adoptioncenter.volunteer-service \
--dependencies=web,webflux,validation \
--version=1.0.0-SNAPSHOT \
volunteer-service

spring init \
--boot-version=3.4.4 \
--build=gradle \
--type=gradle-project \
--java-version=17 \
--packaging=jar \
--name=api-gateway \
--package-name=com.roos.adoptioncenter.apigateway \
--groupId=com.roos.adoptioncenter.apigateway \
--dependencies=web,webflux,validation,hateoas \
--version=1.0.0-SNAPSHOT \
api-gateway