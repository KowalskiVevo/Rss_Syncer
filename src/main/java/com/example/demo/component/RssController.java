package com.example.demo.component;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepos;
import com.example.demo.service.KafkaProducer;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class RssController {

    private final KafkaProducer kafkaProducer;
    private final String url = "https://habr.com/ru/rss/best/";
    private final PostRepos postRepos;

    // Количество минут, через которое производится получение элементов ленты
    private final int N = 5;

    /**
     * Отправляет новые элементы ленты в брокер Kafka каждые N минут
     */
    @Scheduled(fixedRate = N * 1000 * 60)
    public void rssReader()
            throws MalformedURLException, IOException, IllegalArgumentException, FeedException, InterruptedException {
        System.out.println("[" + new Date().toString() + "] Веду поиск новых элементов ленты, Бип-Боп-Бип!");
        XmlReader reader = new XmlReader(new URL(url));
        SyndFeed feed = new SyndFeedInput().build(reader);
        for (SyndEntry entry : feed.getEntries()) {
            if (postRepos.findAll().stream().filter(o -> o.getUrl().equals(entry.getUri())).findFirst()
                    .isPresent() == false) {
                Post post = new Post();
                post.setUrl(entry.getUri().toString());
                postRepos.save(post);
                sendJsonToKafka(entry);
            }
        }

    }

    /**
     * Метод, создающий новый интерфейс SyndFeed с одним элементом entry (С одним
     * элементом item)
     * 
     * @param entry - SyndEntry, содержащий новую новостную страницу
     * @return - SyndFeed, содержащий один @param entry
     */
    private SyndFeed translateRssToSoloXml(SyndEntry entry)
            throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        XmlReader reader = new XmlReader(new URL(url));
        SyndFeed feed = new SyndFeedInput().build(reader);
        feed.getEntries().clear();
        feed.getEntries().add(entry);
        feed.setTitle("Новая популярная публикация за сутки");
        feed.setDescription("Новая популярная публикация за 24 часа");
        feed.setPublishedDate(new Date());
        return feed;
    }

    /**
     * Метод, отправляющий json в брокер Kafka
     * 
     * @param entry - SyndEntry, содержащий новую новостную страниц
     */
    private void sendJsonToKafka(SyndEntry entry)
            throws MalformedURLException, IllegalArgumentException, IOException, FeedException {
        SyndFeed feed = translateRssToSoloXml(entry);
        SyndFeedOutput output = new SyndFeedOutput();

        // Преобразование XML в Json
        String xMLString = output.outputString(feed);
        JSONObject jsonObject = XML.toJSONObject(xMLString);
        String jsonPrettyPrintString = jsonObject.toString(4);

        kafkaProducer.sendOrder(jsonPrettyPrintString);
    }

}
