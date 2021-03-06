package com.anarsoft.race.detection.process

import com.anarsoft.race.detection.process.workflow._;
import com.anarsoft.race.detection.process.monitor._;
import com.anarsoft.race.detection.process.read._;
import com.anarsoft.race.detection.process.method._;
import com.anarsoft.race.detection.process.result._;
import collection.JavaConverters._
import com.anarsoft.race.detection.model.result._;
import com.anarsoft.race.detection.process.syncAction._;
import com.anarsoft.race.detection.process.nonVolatileField._;
import com.anarsoft.race.detection.process.field._;
import com.anarsoft.race.detection.process.workflow.SingleStep
import com.vmlens.api._;
import java.io._;
import scala.collection.mutable.HashMap;
import com.anarsoft.race.detection.process.gen._;
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.anarsoft.race.detection.process.directMemory._;
import com.vmlens.api.internal.reports._;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.ArrayBuffer
import com.anarsoft.race.detection.process.partialOrder.StepCreateNewPartialOrderBuilder4SlidingWindowId
import com.vmlens .api.internal.ModelFilter
import com.anarsoft.race.detection.process.aggregate.ContextBuildAggregate
import com.anarsoft.race.detection.process.detectRaceConditions.ContextDetectRaceConditions
import com.anarsoft.race.detection.process.monitorRelation.ContextCreateMonitorRelation
import com.anarsoft.race.detection.process.detectDeadlocks.ContextDetectDeadlocks
import com.anarsoft.race.detection.process.monitorRelation.StepCreateMonitorRelation
import com.anarsoft.race.detection.process.detectDeadlocks.StepFindPotentialDeadlocks
import com.anarsoft.race.detection.process.detectDeadlocks.StepBuildPotentialDeadlockWithParentMonitorIds
import com.anarsoft.race.detection.process.detectDeadlocks.StepCheckPotentialDeadlocks
import com.anarsoft.race.detection.process.perEventList.PerEventListStepCollection
import com.anarsoft.race.detection.process.sharedState.StepCreateSharedState
import com.anarsoft.race.detection.process.facade.StepCreateFieldAndArrayPerStackTraceFacade
import java.io.File
import com.vmlens.maven.plugin.Extended
import com.anarsoft.race.detection.process.setStacktraceOrdinal.ContextSetStacktraceOrdinal
import net.liftweb.json._;
import net.liftweb.json.Extraction._
import com.anarsoft.race.detection.process.scheduler._
import com.anarsoft.trace.agent.runtime.process.PluginController
import com.anarsoft.trace.agent.runtime.process.AgentController
import com.typesafe.scalalogging.Logger

abstract class ProcessTemplate[RESULT,MAIN_CONTEXT <: ContextProcessTemplate](val prozessConfig : ProzessConfig) {
  
   val logger = Logger("com.vmlens.Performance")
  
     def createAndExecutePipeline(eventDir: String, executorService: ExecutorService, listForFinally: HashMap[Object, ToBeClosed], maxSlidingWindowId: Int,progressMonitor: ProgressMonitor) : RESULT;
  
     def addStartSteps(eventDir : String, executorService: ExecutorService, listForFinally: HashMap[Object, ToBeClosed], maxSlidingWindowId: Int ,processPipeline :  ProcessPipeline[MAIN_CONTEXT]);
     
      
  
     def addEventProcessingSteps(eventDir: String, readMethodEvents: Step[ContextMethodData] , processPipeline : ProcessPipeline[MAIN_CONTEXT], maxSlidingWindowId : Int);
     
     def addStepsBeforeStackTraceGraph( processPipeline  : ProcessPipeline[MAIN_CONTEXT]);
  
     
     def addEndSteps( eventDir : String, processPipeline  : ProcessPipeline[MAIN_CONTEXT]);
     
     
      def additionalOpOnParallizedMethodEnter ( event : ParallizedMethodEnterEvent , context : ContextMethodData );
      def additionalOpOnMethodInParallizeBlock (   event : MethodEvent , parallizedId : Int ,  context : ContextMethodData  );
      
     
     
     def createPipeline(eventDir: String, executorService: ExecutorService, listForFinally: HashMap[Object, ToBeClosed], maxSlidingWindowId: Int) =
     {
       
    
       
            
             val processPipeline = new ProcessPipeline[MAIN_CONTEXT]();

                 processPipeline.step(new StepReadThreadNames(eventDir));
         // processPipeline.step(new StepReadPropertiesAndCreateFilter(eventDir));
          
            processPipeline.step(new WrappedStep(new StepCreateStream[ApplyMethodEventVisitor, ContextMethodData]((x, c) => c.methodEventStreams = x, new MethodDeSerializer(),
        "method_", "methodStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 0), classOf[ContextMethodData]));
          
         processPipeline.step(new StepInitializeMethodData());
                
             
        addStartSteps( eventDir , executorService, listForFinally, maxSlidingWindowId , processPipeline );
        
     
      
    
        processPipeline.step(new StepReadStackTraceEvents(eventDir));
        
              
        processPipeline.step(new WrappedStep(new StepCall[ContextStackTraceGraphBuilder](context => context.initializeContextStackTraceGraphBuilder()), classOf[ContextStackTraceGraphBuilder]));
              
         processPipeline.step(new   StepIncrementProgressMonitor[MAIN_CONTEXT](5));  
              
       // start der Prozessierung der felder in sliding window ids

      val readMethodEvents = readAndProzessMethodEvents(processPipeline);

      // Methods müssen zwei slidingWindow Ids vorher gelesen werden      

      processPipeline.step(readMethodEvents);
      processPipeline.step(readMethodEvents);

       
      addEventProcessingSteps(eventDir, readMethodEvents,processPipeline, maxSlidingWindowId);
      
      addStepsBeforeStackTraceGraph( processPipeline );
      
      processPipeline.step(new StepCreateStackTraceGraph())

     

      processPipeline.step(new StepAddStacktraceIdsAndMissingLink());

         
      processPipeline.step(new   StepIncrementProgressMonitor[MAIN_CONTEXT](5)); 
      
    
      processPipeline.step( new StepReadAgentLog(eventDir,prozessConfig) );
      
      
      addEndSteps(eventDir , processPipeline);
   
       
      processPipeline;
      
     }
  
     
     
  def readAndProzessMethodEvents(processPipeline : ProcessPipeline[MAIN_CONTEXT]) =
    {
      val compositeStep = new CompositeStep[ContextMethodData]("readAndProzessMethodEvents",processPipeline);

       
       
    
        
      compositeStep.step(new StepReadEvents[ApplyMethodEventVisitor, ContextMethodData](x => x.methodEventStreams, (c) => new MethodReadCallback(c,processPipeline)));
      compositeStep.step(new StepProzessMethodEvents(additionalOpOnParallizedMethodEnter,additionalOpOnMethodInParallizeBlock));

      compositeStep

    }
     
  
  
     
   
     
     
     
     
     
     
     
     
     def prozessMavenPlugin(eventDir: String,progressMonitor: ProgressMonitor)  =
  {
       
       
     val state =  PluginController.loadCurrentState(eventDir);  
    
     if( state.getState != AgentController.STATE_STOPPED )
     {
       throw new RuntimeException("vmlens agent is still running");
     }
     
     
       
     prozessWithMonitor(eventDir: String,  progressMonitor,  state.getSlidingWindowId()); 
  }
  

  def prozessAll(eventDir: String )  =
    {
   
    val agentState =  PluginController.loadCurrentState(eventDir) ;
    
     val slidingWindowId = agentState.getSlidingWindowId;
       
 
     
     
     println ( agentState)
    
      prozess(eventDir: String, slidingWindowId)
    }

  def prozess(eventDir: String, maxSlidingWindowId: Int) =
    {
      prozessWithMonitor(eventDir, new SystemOutProgressMonitor(), maxSlidingWindowId);
    }
     
  
  
    def eventDirOK(eventDir: String) = {
    var eventFileCount = 0;
    val dir = new File(eventDir)

    for (file <- dir.listFiles()) {
      if (file.getName().endsWith(".vmlens")) {
        eventFileCount = eventFileCount + 1;
      }
    }

    eventFileCount != 0
  }
  
  /*
  def isMultithreaded(eventDir: String) =
    {
      new File(eventDir + "/fieldStatistic_0.vmlens").exists()
   }
  */
  
   def prozessWithMonitor(eventDir: String, progressMonitor: ProgressMonitor, maxSlidingWindowId: Int) =
    {

     
       
 
      
       

        if (  eventDirOK(eventDir) ) {
          val listForFinally = new HashMap[Object, ToBeClosed]
          val executorService = Executors.newFixedThreadPool(1);

//          val pipeline = createPipeline(eventDir, executorService, listForFinally, maxSlidingWindowId);
//
//          val context = new ContextReadAndProcess();

          try {
            
             logger.debug("maxSlidingWindowId " + maxSlidingWindowId );
            
              createAndExecutePipeline(eventDir, executorService, listForFinally, maxSlidingWindowId,progressMonitor: ProgressMonitor);
           
          } finally {
            for (e <- listForFinally) {
              e._2.close();
            }

            executorService.shutdownNow();

          }

       

        

        } else {
           throw new NoEventFilesException() ;
        }

    }
  
  
}