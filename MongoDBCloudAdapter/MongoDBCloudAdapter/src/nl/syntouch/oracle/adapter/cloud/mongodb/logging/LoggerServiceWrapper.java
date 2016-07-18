package nl.syntouch.oracle.adapter.cloud.mongodb.logging;

/*
Copyright 2016 SynTouch B.V.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import oracle.tip.tools.ide.adapters.cloud.api.service.LoggerService;

public class LoggerServiceWrapper implements CloudAdapterLogger {
    
    private final LoggerService logger;
    
    public LoggerServiceWrapper(LoggerService logger) {
        this.logger = logger;
    }
    
    LoggerService.Level getLevel(CloudAdapterLogger.Level level) {
        if (CloudAdapterLogger.Level.ERROR.equals(level)) {
            return LoggerService.Level.SEVERE;
        }
        
        return LoggerService.Level.valueOf(level.toString());
    }

    @Override
    public void log(CloudAdapterLogger.Level level, String message) {
        logger.log(getLevel(level), message);
    }
}
