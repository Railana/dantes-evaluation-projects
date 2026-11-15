/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.config;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.cassandra.config.DataStorageSpec.DataStorageUnit.MEBIBYTES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ParseAndConvertUnitsTest
{
    @BeforeClass
    public static void setupDatabaseDescriptor()
    {
        DatabaseDescriptor.daemonInitialization();
    }

    // CASSANDRA-15234
    @Test
    public void testConfigurationLoaderParser()
    {
        Config config = DatabaseDescriptor.loadConfig();

        //Confirm duration parameters were successfully parsed with the default values in cassandra.yaml
        assertEquals(new DurationSpec.IntMillisecondsBound(10800000), config.max_hint_window, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(0), config.native_transport_idle_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(10000), config.request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(5000), config.read_request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(10000), config.range_request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(2000), config.write_request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(5000), config.counter_write_request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(1800), config.cas_contention_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(60000), config.truncate_request_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntSecondsBound(300), config.streaming_keep_alive_period, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(500), config.slow_query_log_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(2000), config.internode_tcp_connect_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(30000), config.internode_tcp_user_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(0), config.commitlog_sync_group_window, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(0), config.commitlog_sync_period, () -> "Add assertion message");
        assertNull(config.periodic_commitlog_sync_lag_block, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(250), config.cdc_free_space_check_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(100), config.dynamic_snitch_update_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(600000), config.dynamic_snitch_reset_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(200), config.gc_log_threshold, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(10000), config.hints_flush_period, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(1000), config.gc_warn_threshold, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntSecondsBound(86400), config.trace_type_query_ttl, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntSecondsBound(604800), config.trace_type_repair_ttl, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(2000), config.permissions_validity, () -> "Add assertion message");
        assertNull(config.permissions_update_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(2000), config.roles_validity, () -> "Add assertion message");
        assertNull(config.roles_update_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMillisecondsBound(2000), config.credentials_validity, () -> "Add assertion message");
        assertNull(config.credentials_update_interval, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntMinutesBound(60), config.index_summary_resize_interval, () -> "Add assertion message");
        assertEquals(DurationSpec.IntSecondsBound.inSecondsString("4h"), config.key_cache_save_period, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntSecondsBound(30), config.cache_load_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(1500), config.user_defined_functions_fail_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.LongMillisecondsBound(500), config.user_defined_functions_warn_timeout, () -> "Add assertion message");
        assertEquals(new DurationSpec.IntSecondsBound(3600), config.validation_preview_purge_head_start, () -> "Add assertion message");

        //Confirm space parameters were successfully parsed with the default values in cassandra.yaml
        assertNull(config.memtable_heap_space, () -> "Add assertion message");
        assertNull(config.memtable_offheap_space, () -> "Add assertion message");
        assertNull(config.repair_session_space, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(4194304), config.internode_application_send_queue_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(134217728), config.internode_application_send_queue_reserve_endpoint_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(536870912), config.internode_application_send_queue_reserve_global_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(4194304), config.internode_application_receive_queue_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(134217728), config.internode_application_receive_queue_reserve_endpoint_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(536870912), config.internode_application_receive_queue_reserve_global_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(16), config.native_transport_max_frame_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(256), config.max_value_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(4), config.column_index_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(2), config.column_index_cache_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(5), config.batch_size_warn_threshold, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(50), config.batch_size_fail_threshold, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(100), config.compaction_large_partition_warning_threshold, () -> "Add assertion message");
        assertNull(config.commitlog_total_space, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(5), config.commitlog_segment_size, () -> "Add assertion message");
        assertNull(config.max_mutation_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(0), config.cdc_total_space, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(1024), config.hinted_handoff_throttle, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(1024), config.batchlog_replay_throttle, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntKibibytesBound(10240), config.trickle_fsync_interval, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntMebibytesBound(50), config.sstable_preemptive_open_interval, () -> "Add assertion message");
        assertNull(config.counter_cache_size, () -> "Add assertion message");
        assertNull(config.file_cache_size, () -> "Add assertion message");
        assertNull(config.index_summary_capacity, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.LongMebibytesBound(1), config.prepared_statements_cache_size, () -> "Add assertion message");
        assertNull(config.key_cache_size, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.LongMebibytesBound(16), config.row_cache_size, () -> "Add assertion message");
        assertNull(config.native_transport_max_request_data_in_flight, () -> "Add assertion message");
        assertNull(config.native_transport_max_request_data_in_flight_per_ip, () -> "Add assertion message");
        assertEquals(new DataStorageSpec.IntBytesBound(1, MEBIBYTES), config.native_transport_receive_queue_capacity, () -> "Add assertion message");

        //Confirm rate parameters were successfully parsed with the default values in cassandra.yaml
        assertEquals(new DataRateSpec.LongBytesPerSecondBound(0), config.compaction_throughput, () -> "Add assertion message");
        assertEquals(new DataRateSpec.LongBytesPerSecondBound(23841858, DataRateSpec.DataRateUnit.MEBIBYTES_PER_SECOND), config.stream_throughput_outbound, () -> "Add assertion message");
        assertEquals(new DataRateSpec.LongBytesPerSecondBound(24, DataRateSpec.DataRateUnit.MEBIBYTES_PER_SECOND), config.inter_dc_stream_throughput_outbound, () -> "Add assertion message");
    }
}