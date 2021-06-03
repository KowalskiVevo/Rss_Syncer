package com.example.demo.controller;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctc.wstx.sw.XmlWriter;
import com.example.demo.service.KafkaProducer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

@RestController
public class RssController {

    @Autowired
    private KafkaProducer kafkaProducer;
    private String url = "https://habr.com/ru/rss/best/";

    @GetMapping("/main")
    public void rssReader(HttpServletRequest request, HttpServletResponse response)
            throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        XmlReader reader = new XmlReader(new URL(url));
        SyndFeed feed = new SyndFeedInput().build(reader);
        // for (SyndEntry entry : feed.getEntries()) {
        // System.out.println(entry.getUri());
        // System.out.println("***********************************");
        // }
        // System.out.println(feed.getEntries().get(0));

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/rss+xml");
        response.setCharacterEncoding("UTF-8");

        // Создаем новую XML с одним Entry
        reader = new XmlReader(new URL(url));
        SyndFeed feed2 = new SyndFeedInput().build(reader);
        feed2.getEntries().clear();
        feed2.getEntries().add(feed.getEntries().get(0));
        feed2.setTitle("Новая популярная публикация за сутки");
        feed2.setDescription("Новая популярная публикация за 24 часа");
        feed2.setPublishedDate(new Date());
        SyndFeedOutput output = new SyndFeedOutput();

        // Вывод полученной XML на /main
        output.output(feed2, response.getWriter());
        System.out.println("****************************");

        // Преобразование XML в Json
        String xMLString = output.outputString(feed2);
        // System.out.println(xMLString);
        JSONObject jsonObject = XML.toJSONObject(xMLString);
        String jsonPrettyPrintString = jsonObject.toString(4);
        System.out.println(jsonPrettyPrintString);

        // Отправляем json в топик брокера кафка
        kafkaProducer.sendOrder(jsonPrettyPrintString);

    }

}
