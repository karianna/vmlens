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
import com.anarsoft.race.detection.process.partialOrder.ContextCreatePartialOrder
import com.anarsoft.race.detection.process.interleave._
import com.anarsoft.config.ConfigValues
import com.anarsoft.race.detection.process.state.StepReadClassName
import com.anarsoft.race.detection.process.mode.state.StepBuildFieldAndArrayFacade4State
/**
 *
 * einlesen und prozessieren der events:
 *
 * 	1) monitor files
 * 	2) method, sync statements, fields
 *  3) description
 *
 *
 *
 *
 */

class ReadAndProcessEvents( prozessConfig : ProzessConfig, val configValues : ConfigValues) extends ProcessTemplate[ModelFacadeAll,MainContextReadAndProcess](prozessConfig) {

  
     def additionalOpOnParallizedMethodEnter ( event : ParallizedMethodEnterEvent , context : ContextMethodData )
     {
       
     }
     
     def additionalOpOnMethodInParallizeBlock (   event : MethodEvent , parallizedId : Int ,  context : ContextMethodData  )
    {

    }
     
  
  
    val perEventListSteps =  PerEventListStepCollection();
  
  
    def addStartSteps(eventDir : String, executorService: ExecutorService, listForFinally: HashMap[Object, ToBeClosed], maxSlidingWindowId: Int ,processPipeline :  ProcessPipeline[MainContextReadAndProcess])
    {
      
      processPipeline.step(new WrappedStep(new StepCall[ContextLastProgramCounter](context => context.initializeContextLastProgramCounter()), classOf[ContextLastProgramCounter]));

      //   Create Heap Dump   
      //      processPipeline.step(new   StepCreateHeapDump());

      processPipeline.step(new WrappedStep(new StepCall[ContextProcessSyncAction](context => context.initializeContextSyncAction()), classOf[ContextProcessSyncAction]));

      
      processPipeline.step(new WrappedStep(new StepCreateStream[MonitorEvent, ContextMonitor]((x, c) => c.monitorEventStreams = x, new MonitorDeSerializer(),
        "monitor_", "monitorStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 1), classOf[ContextMonitor]));
      
      
      processPipeline.step(new WrappedStep(new StepCreateStream[SyncAction, ContextProcessSyncAction]((x, c) => c.syncActionStreams = x, new SyncActionsDeSerializer(),
        "syncActions_", "syncActionsStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 1), classOf[ContextProcessSyncAction]));

    
    
      processPipeline.step(new WrappedStep(new StepCreateStream[ApplyFieldEventVisitor, ContextNonVolatileFields]((x, c) => c.fieldEventStreams = x, new FieldDeSerializer(),
        "field_", "fieldStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 5), classOf[ContextNonVolatileFields]));

      processPipeline.step(new WrappedStep(new StepCreateStream[DirectMemoryEvent, ContextReadDirectMemory]((x, c) => c.directMemoryEventStreams = x, new DirectMemoryDeSerializer(),
        "directMemory_", "directMemoryStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 2), classOf[ContextReadDirectMemory]));
     

       processPipeline.step(new WrappedStep(new StepCreateStream[SchedulerEvent, ContextSchedulerRead]((x, c) => c.schedulerEventStreams = x, new SchedulerDeSerializer(),
        "scheduler_", "schedulerStatistic_", eventDir, executorService, listForFinally, maxSlidingWindowId, 2), classOf[ContextSchedulerRead]));
      
       processPipeline.step(new StepCall[ContextCreatePartialOrder](  c => c.initializeContextCreatePartialOrder() )); 
      processPipeline.step(new StepCall[ContextBuildAggregate](  c => c.initializeContextBuildAggregate() ));
      processPipeline.step(new StepCall[ContextDetectRaceConditions](  c => c.initializeContextDetectRaceConditions() ));
      processPipeline.step(new StepCall[ContextFieldIdMap](  c => c.initializeContextFieldIdMap() ));
       
      

      
      

      
      processPipeline.step(new StepCall[ContextCreateMonitorRelation](  c => c.initializeContextCreateMonitorRelation() ));
      processPipeline.step(new StepCall[ContextDetectDeadlocks](  c => c.initializeContextDetectDeadlocks() ));
      
      processPipeline.step(new StepCall[ContextInterleave](  c => c.initializeContextInterleave() ));
      
      
     // processPipeline.step(new StepCall[ContextInterleave](  c => c.initializeContextMergeEvents4Statements() ));
       
    }
    
    
  

  def addEventProcessingSteps(eventDir: String, readMethodEvents: Step[ContextMethodData], processPipeline: ProcessPipeline[MainContextReadAndProcess],maxSlidingWindowId: Int ) {
    val stepReadAndProzessSyncActionAndMonitorEvents = readAndProzessSyncActionAndMonitorEvents(perEventListSteps,processPipeline);

    processPipeline.step(stepReadAndProzessSyncActionAndMonitorEvents);

    processPipeline.step(new Loop[ContextReadMethodAndField](createReadMethodAndFieldEventPipeline(eventDir, readMethodEvents, stepReadAndProzessSyncActionAndMonitorEvents, perEventListSteps,processPipeline), 
        classOf[ContextReadMethodAndField] , maxSlidingWindowId , processPipeline ));

  }
     
  
    def addStepsBeforeStackTraceGraph( processPipeline  : ProcessPipeline[MainContextReadAndProcess])
    {

    }
     
  
  
  
  
  def addEndSteps( eventDir : String, processPipeline  : ProcessPipeline[MainContextReadAndProcess] ) 
    {

       
      processPipeline.step(new StepProcessInterleaveEventListAfterRead());
    
    
       processPipeline.step( new StepReadClassName(eventDir) ); 
      
          processPipeline.step(new StepReadDescription(eventDir));
     
     
        processPipeline.step(new StepBuildFieldAndArrayFacade4State());
     
     
//     processPipeline.step( new StepCreateStatementGroupsAndParallizedMethods());
     
     
   
        // Parallize Logic
        
        
        
  
       
       
      processPipeline.step(new StepCreateModelFacade(configValues));
          
      
     
      

    
    }


  def readAndProzessSyncActionAndMonitorEvents(perEventListSteps : PerEventListStepCollection, processPipeline  : ProcessPipeline[MainContextReadAndProcess]) =
    {

      val compositeStep = new CompositeStep[ContextReadAndProzessSyncActionAndMonitorEvents]("readAndProzessSyncActionAndMonitorEvents",processPipeline);

      
       compositeStep.step(new StepCreateNewPartialOrderBuilder4SlidingWindowId());
      //     processPipeline.step(new WrappedStep(new StepCreateStream[MonitorEvent,ContextMonitorData]((x, c) => c.monitorEventStreams = x, new MonitorDeSerializer() ,
      //          "monitor_", "monitorStatistic_", eventDir ,executorService ,  listForFinally , maxSlidingWindowId, 1) , classOf[ContextMonitorData]));

 //     compositeStep.step(new StepReadEvents[DirectMemoryEvent, ContextReadDirectMemory](x => x.directMemoryEventStreams, (c) => new DirectMemoryReadCallback(c)));
      compositeStep.step(new StepReadEvents[SyncAction, ContextProcessSyncAction](x => x.syncActionStreams, (c) => new SyncActionReadCallback(c,processPipeline)));
      compositeStep.step(new StepReadEvents[MonitorEvent, ContextMonitor](x => x.monitorEventStreams, (c) => new MonitorReadCallback(c,processPipeline)));

      
      compositeStep.step(perEventListSteps.setArrayOrdinalInterleave);
      compositeStep.step(perEventListSteps.setFieldOrdinal);
      compositeStep.step(perEventListSteps.setStacktraceOrdinal);
      compositeStep.step(perEventListSteps.buildMethodOrdinalAggregate);
      
       //compositeStep.step(perEventListSteps.newBuildMethodOrdinalAggregate);
      
    //  compositeStep.step(perEventListSteps.buildStackTraceOrdinalAggregate);
      
      
      compositeStep.step(new StepCreateMonitorRelation())
      compositeStep.step(new StepFindPotentialDeadlocks())
      compositeStep.step(new StepBuildPotentialDeadlockWithParentMonitorIds())
      compositeStep.step(new StepCheckPotentialDeadlocks())
      
      
      
      compositeStep.step(perEventListSteps.setMonitorInfo4Volatile);
      
      
      prozessConfig.checkLocksStep() match
      {
        case None =>
          {
            
          }
        case Some(x) =>
          {
                compositeStep.step(x);
          }
        
        
      }
      
      compositeStep.step(perEventListSteps.prozessSyncPointLists);
      compositeStep.step(new StepProzessThreadStoppedEvent());
      
      compositeStep.step(new StepAddCurrentlyBuilded2PartialOrder());
      
      




      compositeStep;
    }

  def createReadMethodAndFieldEventPipeline(eventDir: String, readMethodEvents: Step[ContextMethodData], stepReadAndProzessSyncActionAndMonitorEvents : Step[ContextReadAndProzessSyncActionAndMonitorEvents],
      perEventListSteps : PerEventListStepCollection , pipeline : ProcessPipeline[_] ) =
    {
      val processPipeline = new ProcessPipeline[ContextReadMethodAndField]();
      processPipeline.step(readMethodEvents);
      processPipeline.step(new StepReadEvents[ApplyFieldEventVisitor, ContextNonVolatileFields](x => x.fieldEventStreams, (c) => new FieldReadCallback(c,pipeline)));
      
          processPipeline.step(new StepReadEvents[SchedulerEvent, ContextSchedulerRead](x => x.schedulerEventStreams, (c) => new SchedulerReadCallback(c)));
      
      
      processPipeline.step(new StepFilterNonVolatileFields());
      
       processPipeline.step(new StepAddNonVolatileFields2InterleaveList());
      
      
      // Actung muss nach einlesen kommen (Ansonsten stimmt die slidingWindowId nicht)
      processPipeline.step(stepReadAndProzessSyncActionAndMonitorEvents);


       processPipeline.step(perEventListSteps.detectRaceConditions);
       processPipeline.step(perEventListSteps.setMonitorInfo4NonVolatile);
      
        processPipeline.step(new StepProcessInterleaveEventListDuringRead());
     
     // processPipeline.step(new StepPrintPartialOrder());
    
      
   
   
   
      processPipeline;
    }

 
  
     def createAndExecutePipeline(eventDir: String, executorService: ExecutorService, listForFinally: HashMap[Object, ToBeClosed], maxSlidingWindowId: Int,progressMonitor: ProgressMonitor) =
   {
          val pipeline = createPipeline(eventDir, executorService, listForFinally, maxSlidingWindowId );

          val context = new MainContextReadAndProcess();

            pipeline.execute(context, progressMonitor);
           
            context.modelFacade;
            
   }
  
 

}

object ReadAndProcessEvents {
  
  
  def create4Test( configValues : ConfigValues  ) = {
     new ReadAndProcessEvents(new ProzessConfigTest(),configValues);
  }
  
  
   def create4Prod( configValues : ConfigValues  ) = {
     new ReadAndProcessEvents(new ProzessConfigProd(),configValues);
  }
  
  
  
  def create4ItTest(   ) = {
     new ReadAndProcessEvents(new ProzessConfigTest(),new TestConfigValues());
  }
  
  
  
  
  
  
  
    
   
   


}