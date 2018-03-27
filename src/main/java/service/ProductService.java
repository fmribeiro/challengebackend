package service;

import beans.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private static final String URL = "http://www.mocky.io/v2/5817803a1000007d01cc7fc9";

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @RequestMapping(value = "challenge-backend/item", method = RequestMethod.GET)
    List<Product> getProductByDate(@RequestParam(value = "begindate") String beginDate,
                                   @RequestParam(value = "finaldate") String finalDate,
                                   RestTemplate restTemplate) {

        Product[] productsArray = restTemplate.getForObject(
                URL, Product[].class);
        List<Product> products = new ArrayList<Product>();


        for (int i = 0; i < productsArray.length; i++) {
            Product product = productsArray[i];
            String prodDate = product.getDate();

            try {
                //String defaultTimezone = TimeZone.getDefault().getID();
                Date productDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(prodDate.replaceAll("Z$", "+0000"));
                Calendar productDateCal = convertDateToCalendar(productDate);

                Calendar dataInicio = convertDateToCalendar(convertStringToDate(beginDate));
                Calendar dataFim = convertDateToCalendar(convertStringToDate(finalDate));

                //verifica se o begindate eh >= a data do produto
                boolean gteInitialDay = productDateCal.get(Calendar.YEAR) == dataInicio.get(Calendar.YEAR) &&
                        productDateCal.get(Calendar.DAY_OF_YEAR) >= dataInicio.get(Calendar.DAY_OF_YEAR);

                //verifica se o finaldate eh <= a data do produto
                boolean lteFinalDay = productDateCal.get(Calendar.YEAR) == dataFim.get(Calendar.YEAR) &&
                        productDateCal.get(Calendar.DAY_OF_YEAR) <= dataFim.get(Calendar.DAY_OF_YEAR);

                if (gteInitialDay && lteFinalDay) {
                    products.add(product);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return products;
    }

    public Date convertStringToDate(String filterDate) {
        Date date = new Date();
        try {
            date = (new SimpleDateFormat("dd-MM-yyyy")).parse(filterDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Calendar convertDateToCalendar(Date date) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        return dateCalendar;
    }

}
