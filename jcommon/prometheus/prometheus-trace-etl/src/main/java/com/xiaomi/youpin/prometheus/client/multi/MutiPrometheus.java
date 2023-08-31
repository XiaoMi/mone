package com.xiaomi.youpin.prometheus.client.multi;

import com.xiaomi.youpin.prometheus.client.Metrics;
import com.xiaomi.youpin.prometheus.client.MetricsManager;
import com.xiaomi.youpin.prometheus.client.PrometheusCounter;
import com.xiaomi.youpin.prometheus.client.PrometheusGauge;
import com.xiaomi.youpin.prometheus.client.PrometheusHistogram;
import com.xiaomi.youpin.prometheus.client.XmCounter;
import com.xiaomi.youpin.prometheus.client.XmGauge;
import com.xiaomi.youpin.prometheus.client.XmHistogram;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangxiaowei
 */
@Slf4j
public class MutiPrometheus implements MetricsManager {

   public static final int CONST_LABELS_NUM = 2;
   public Map<String, String> constLabels;
   public Map<String, Object> prometheusMetrics;
   public Map<String,Object> prometheusTypeMetrics;

   private CollectorRegistry registry;

   private byte[] lock = new byte[0];
   private byte[] typeLock = new byte[0];

   public MutiPrometheus() {
      this.prometheusMetrics = new ConcurrentHashMap<>();
      this.prometheusTypeMetrics = new ConcurrentHashMap<>();
   }

   public MutiPrometheus(CollectorRegistry registry) {
      this.prometheusMetrics = new ConcurrentHashMap<>();
      this.prometheusTypeMetrics = new ConcurrentHashMap<>();
      this.registry = registry;
   }

   @Override
   public XmCounter newCounter(String metricName, String... labelName) {
      if (prometheusTypeMetrics.containsKey(metricName)) {
         return (XmCounter) prometheusTypeMetrics.get(metricName);
      }
      synchronized (typeLock) {
         PrometheusCounter prometheusCounter = new PrometheusCounter(getCounter(metricName, labelName), labelName, null);
         prometheusTypeMetrics.put(metricName,prometheusCounter);
         return prometheusCounter;
      }
   }

   @Override
   public XmGauge newGauge(String metricName, String... labelName) {
      if (prometheusTypeMetrics.containsKey(metricName)) {
         return (XmGauge) prometheusTypeMetrics.get(metricName);
      }
      synchronized (typeLock) {
         PrometheusGauge prometheusGauge = new PrometheusGauge(getGauge(metricName, labelName), labelName, null);
         prometheusTypeMetrics.put(metricName,prometheusGauge);
         return prometheusGauge;
      }

   }

   @Override
   public XmHistogram newHistogram(String metricName, double[] bucket, String... labelNames) {
      if (prometheusTypeMetrics.containsKey(metricName)) {
         return (XmHistogram) prometheusTypeMetrics.get(metricName);
      }
      synchronized (typeLock) {
         PrometheusHistogram prometheusHistogram = new PrometheusHistogram(getHistogram(metricName, bucket, labelNames), labelNames, null);
         prometheusTypeMetrics.put(metricName,prometheusHistogram);
         return prometheusHistogram;
      }
   }

   public Counter getCounter(String metricName, String... labelName) {
      if (constLabels.size() != MutiPrometheus.CONST_LABELS_NUM) {
         return null;
      }
      try {
         //如果有直接返回
         if (prometheusMetrics.containsKey(metricName)) {
            //log.debug("already have metric:" + metricName);
            return (Counter) prometheusMetrics.get(metricName);
         }

         synchronized (lock) {
            //没有需要先注册一个
            List<String> mylist = new ArrayList<>(Arrays.asList(labelName));
//            mylist.add(Metrics.APPLICATION);
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            Counter newCounter = Counter.build()
                    .name(metricName)
                    .namespace(constLabels.get(Metrics.GROUP) + "_" + constLabels.get(Metrics.SERVICE))
                    .labelNames(finalValue)
                    .help(metricName)
                    .register(registry);

            prometheusMetrics.put(metricName, newCounter);
            return newCounter;
         }
      } catch (Throwable throwable) {
         log.warn(throwable.getMessage());
         return null;
      }
   }

   public Gauge getGauge(String metricName, String... labelName) {
      if (constLabels.size() != MutiPrometheus.CONST_LABELS_NUM) {
         return null;
      }
      try {
         //如果有直接返回
         if (prometheusMetrics.containsKey(metricName)) {
           // log.debug("already have metric:" + metricName);
            return (Gauge) prometheusMetrics.get(metricName);
         }
         synchronized (lock) {
            //没有需要先注册一个
            List<String> mylist = new ArrayList<>(Arrays.asList(labelName));
//            mylist.add(Metrics.APPLICATION);
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            Gauge newGauge = Gauge.build()
                    .name(metricName)
                    .namespace(constLabels.get(Metrics.GROUP) + "_" + constLabels.get(Metrics.SERVICE))
                    .labelNames(finalValue)
                    .help(metricName)
                    .register(registry);

            prometheusMetrics.put(metricName, newGauge);
            return newGauge;
         }
      } catch (Throwable throwable) {
         log.warn(throwable.getMessage());
         return null;
      }

   }

   public Histogram getHistogram(String metricName, double[] buckets, String... labelNames) {
      if (constLabels.size() != MutiPrometheus.CONST_LABELS_NUM) {
         return null;
      }
      try {
         //如果有直接返回
         if (prometheusMetrics.containsKey(metricName)) {
           // log.debug("already have metric:" + metricName);
            return (Histogram) prometheusMetrics.get(metricName);
         }

         synchronized (lock) {
            //没有需要注册一个
            List<String> mylist = new ArrayList<>(Arrays.asList(labelNames));
//            mylist.add(Metrics.APPLICATION);
            String[] finalValue = mylist.toArray(new String[mylist.size()]);
            Histogram newHistogram = Histogram.build()
                    .buckets(buckets)
                    .name(metricName)
                    .namespace(constLabels.get(Metrics.GROUP) + "_" + constLabels.get(Metrics.SERVICE))
                    .labelNames(finalValue)
                    .help(metricName)
                    .register(registry);
            prometheusMetrics.put(metricName, newHistogram);
            return newHistogram;
         }
      } catch (Throwable throwable) {
         log.warn(throwable.getMessage());
         return null;
      }
   }
}
