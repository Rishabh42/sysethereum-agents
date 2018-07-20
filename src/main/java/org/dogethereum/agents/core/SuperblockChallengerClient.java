package org.dogethereum.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.dogethereum.agents.constants.SystemProperties;
import org.dogethereum.agents.core.dogecoin.*;
import org.dogethereum.agents.core.eth.EthWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Monitors the Ethereum blockchain for superblock-related events
 * and challenges invalid submissions.
 * @author Catalina Juarros
 * @author Ismael Bejarano
 */

@Service
@Slf4j(topic = "SuperblockChallengerClient")
public class SuperblockChallengerClient extends SuperblockClientBase {

    public SuperblockChallengerClient() {
        super("Superblock challenger client");
    }

    @Override
    public void task(long fromBlock, long toBlock) {
        try {
            latestEthBlockProcessed = toBlock;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /* ---- STATUS SETTERS ---- */

    /* ---- CONFIRMING/DEFENDING ---- */

    /* ---- OVERRRIDE ABSTRACT METHODS ---- */

    @Override
    protected void setupClient() {}

    @Override
    protected Boolean isEnabled() {
        return config.isDogeBlockChallengerEnabled();
    }

    @Override
    protected String getLastEthBlockProcessedFilename() {
        return "SuperblockChallengerLatestEthBlockProcessedFile.dat";
    }
}
