package com.streaming.predictitstream.kafkaConsumer;

import com.streaming.predictitstream.PredictItStreamApplication;
import com.streaming.predictitstream.entities.Contract;
import com.streaming.predictitstream.entities.ContractMarket;
import com.streaming.predictitstream.services.ContractHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for consuming data from PredictIt
 */
@Component
public class PredictItConsumer {

    private final static Logger LOGGER = Logger.getLogger(PredictItStreamApplication.class.getName());

    @Resource
    ContractHandler contractHandler;

    @Resource
    @Qualifier("CollectionsBean")
    List<String> contractNames;


    /**
     * Method for consuming all topics beginning with president.* and saving it to the to the same table
     * @param record record of type contractmarket with the data
     */
    @KafkaListener(topicPattern= "president.*", groupId = "group-1", containerFactory = "kafkaListenerContainerFactory")
    public void listenPresidentTopics(ContractMarket record) {
        LOGGER.log(Level.INFO, "Recieved message from " + record.getShortName()+ " at "+record.getTimeStamp());
        LocalDateTime timestamp = record.getTimeStamp();
        int counter = 0;
        for (Contract contract : record.getContracts()) {
            if (contractNames.contains(contract.getName()) || counter < 5) {
                contractHandler.saveContract(contract, timestamp);
            }
            counter++;

        }
    }
}
