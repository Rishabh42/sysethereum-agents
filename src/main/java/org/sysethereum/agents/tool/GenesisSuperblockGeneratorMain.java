package org.sysethereum.agents.tool;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.core.syscoin.Keccak256Hash;
import org.sysethereum.agents.core.syscoin.Superblock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.bitcoinj.core.Utils.HEX;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@Slf4j(topic = "GenesisSuperblockGeneratorMain")
/**
 * Tool to create a genesis superblock
 * @author Catalina Juarros
 * @author Oscar Guindzberg
 */
public class GenesisSuperblockGeneratorMain {
    private static final Logger log = LoggerFactory.getLogger("LocalAgentConstants");
    private static String baseDir = "/yourPath/sysethereum-agents";
    private static String subDir = "/src/main/java/org/sysethereum/agents/tool";

    public static void main(String[] args) throws Exception {
        SystemProperties config = SystemProperties.CONFIG;
        log.info("Running GenesisSuperblockGeneratorMain version: {}-{}", config.projectVersion(), config.projectVersionModifier());
        // Instantiate the spring context
        AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext();
        c.register(SyscoinWrapper.class);
        c.refresh();
        SyscoinWrapper syscoinWrapper = c.getBean(SyscoinWrapper.class);
        Superblock s = getGenesisSuperblock(syscoinWrapper);
        s.getSuperblockId();
        System.out.println(s);
    }

    private static Superblock getGenesisSuperblock(SyscoinWrapper syscoinWrapper) throws IOException, BlockStoreException {
        SystemProperties config = SystemProperties.CONFIG;
        AgentConstants agentConstants = config.getAgentConstants();
        NetworkParameters params = agentConstants.getSyscoinParams();

        BufferedReader reader = new BufferedReader(
                new FileReader(baseDir + subDir + "/syscoinmain-2309215-to-2309216"));
        List<Sha256Hash> syscoinBlockHashes = parseBlockHashes(reader);
        Keccak256Hash genesisParentHash = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        StoredBlock lastSyscoinBlock = syscoinWrapper.getBlock(syscoinBlockHashes.get(syscoinBlockHashes.size() - 1));
        StoredBlock previousToLastSyscoinBlock = syscoinWrapper.getBlock(lastSyscoinBlock.getHeader().getPrevBlockHash());

        return new Superblock(params,
                syscoinBlockHashes,
                lastSyscoinBlock.getChainWork(),
                lastSyscoinBlock.getHeader().getTimeSeconds(),
                previousToLastSyscoinBlock.getHeader().getTimeSeconds(),
                lastSyscoinBlock.getHeader().getDifficultyTarget(),
                genesisParentHash,
                0, lastSyscoinBlock.getHeight());
    }

    private static List<Sha256Hash> parseBlockHashes(BufferedReader reader) throws IOException {
        List<Sha256Hash> result = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            byte[] rawBytes = HEX.decode(line);
            result.add(Sha256Hash.wrap(rawBytes));
            line = reader.readLine();
        }
        return result;
    }
}