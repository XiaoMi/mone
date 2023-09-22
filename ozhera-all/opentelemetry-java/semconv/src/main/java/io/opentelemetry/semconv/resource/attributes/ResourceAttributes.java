/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.semconv.resource.attributes;

import static io.opentelemetry.api.common.AttributeKey.longKey;
import static io.opentelemetry.api.common.AttributeKey.stringArrayKey;
import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.common.AttributeKey;
import java.util.List;

// DO NOT EDIT, this is an Auto-generated file from
// buildscripts/semantic-convention/templates/SemanticAttributes.java.j2
public final class ResourceAttributes {

  /** Name of the cloud provider. */
  public static final AttributeKey<String> CLOUD_PROVIDER = stringKey("cloud.provider");

  /** The cloud account ID the resource is assigned to. */
  public static final AttributeKey<String> CLOUD_ACCOUNT_ID = stringKey("cloud.account.id");

  /**
   * The geographical region the resource is running. Refer to your provider&#39;s docs to see the
   * available regions, for example [AWS
   * regions](https://aws.amazon.com/about-aws/global-infrastructure/regions_az/), [Azure
   * regions](https://azure.microsoft.com/en-us/global-infrastructure/geographies/), or [Google
   * Cloud regions](https://cloud.google.com/about/locations).
   */
  public static final AttributeKey<String> CLOUD_REGION = stringKey("cloud.region");

  /**
   * Cloud regions often have multiple, isolated locations known as zones to increase availability.
   * Availability zone represents the zone where the resource is running.
   *
   * <p>Note: Availability zones are called &#34;zones&#34; on Google Cloud.
   */
  public static final AttributeKey<String> CLOUD_AVAILABILITY_ZONE =
      stringKey("cloud.availability_zone");

  /**
   * The cloud platform in use.
   *
   * <p>Note: The prefix of the service SHOULD match the one specified in `cloud.provider`.
   */
  public static final AttributeKey<String> CLOUD_PLATFORM = stringKey("cloud.platform");

  /**
   * The Amazon Resource Name (ARN) of an [ECS container
   * instance](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_instances.html).
   */
  public static final AttributeKey<String> AWS_ECS_CONTAINER_ARN =
      stringKey("aws.ecs.container.arn");

  /**
   * The ARN of an [ECS
   * cluster](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/clusters.html).
   */
  public static final AttributeKey<String> AWS_ECS_CLUSTER_ARN = stringKey("aws.ecs.cluster.arn");

  /**
   * The [launch
   * type](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/launch_types.html) for an ECS
   * task.
   */
  public static final AttributeKey<String> AWS_ECS_LAUNCHTYPE = stringKey("aws.ecs.launchtype");

  /**
   * The ARN of an [ECS task
   * definition](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html).
   */
  public static final AttributeKey<String> AWS_ECS_TASK_ARN = stringKey("aws.ecs.task.arn");

  /** The task definition family this task definition is a member of. */
  public static final AttributeKey<String> AWS_ECS_TASK_FAMILY = stringKey("aws.ecs.task.family");

  /** The revision for this task definition. */
  public static final AttributeKey<String> AWS_ECS_TASK_REVISION =
      stringKey("aws.ecs.task.revision");

  /** The ARN of an EKS cluster. */
  public static final AttributeKey<String> AWS_EKS_CLUSTER_ARN = stringKey("aws.eks.cluster.arn");

  /**
   * The name(s) of the AWS log group(s) an application is writing to.
   *
   * <p>Note: Multiple log groups must be supported for cases like multi-container applications,
   * where a single application has sidecar containers, and each write to their own log group.
   */
  public static final AttributeKey<List<String>> AWS_LOG_GROUP_NAMES =
      stringArrayKey("aws.log.group.names");

  /**
   * The Amazon Resource Name(s) (ARN) of the AWS log group(s).
   *
   * <p>Note: See the [log group ARN format
   * documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/iam-access-control-overview-cwl.html#CWL_ARN_Format).
   */
  public static final AttributeKey<List<String>> AWS_LOG_GROUP_ARNS =
      stringArrayKey("aws.log.group.arns");

  /** The name(s) of the AWS log stream(s) an application is writing to. */
  public static final AttributeKey<List<String>> AWS_LOG_STREAM_NAMES =
      stringArrayKey("aws.log.stream.names");

  /**
   * The ARN(s) of the AWS log stream(s).
   *
   * <p>Note: See the [log stream ARN format
   * documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/iam-access-control-overview-cwl.html#CWL_ARN_Format).
   * One log group can contain several log streams, so these ARNs necessarily identify both a log
   * group and a log stream.
   */
  public static final AttributeKey<List<String>> AWS_LOG_STREAM_ARNS =
      stringArrayKey("aws.log.stream.arns");

  /** Container name. */
  public static final AttributeKey<String> CONTAINER_NAME = stringKey("container.name");

  /**
   * Container ID. Usually a UUID, as for example used to [identify Docker
   * containers](https://docs.docker.com/engine/reference/run/#container-identification). The UUID
   * might be abbreviated.
   */
  public static final AttributeKey<String> CONTAINER_ID = stringKey("container.id");

  /** The container runtime managing this container. */
  public static final AttributeKey<String> CONTAINER_RUNTIME = stringKey("container.runtime");

  /** Name of the image the container was built on. */
  public static final AttributeKey<String> CONTAINER_IMAGE_NAME = stringKey("container.image.name");

  /** Container image tag. */
  public static final AttributeKey<String> CONTAINER_IMAGE_TAG = stringKey("container.image.tag");

  /**
   * Name of the [deployment environment](https://en.wikipedia.org/wiki/Deployment_environment) (aka
   * deployment tier).
   */
  public static final AttributeKey<String> DEPLOYMENT_ENVIRONMENT =
      stringKey("deployment.environment");

  /** The name of the function being executed. */
  public static final AttributeKey<String> FAAS_NAME = stringKey("faas.name");

  /**
   * The unique ID of the function being executed.
   *
   * <p>Note: For example, in AWS Lambda this field corresponds to the
   * [ARN](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html) value, in GCP
   * to the URI of the resource, and in Azure to the
   * [FunctionDirectory](https://github.com/Azure/azure-functions-host/wiki/Retrieving-information-about-the-currently-running-function)
   * field.
   */
  public static final AttributeKey<String> FAAS_ID = stringKey("faas.id");

  /**
   * The version string of the function being executed as defined in [Version
   * Attributes](../../resource/semantic_conventions/README.md#version-attributes).
   */
  public static final AttributeKey<String> FAAS_VERSION = stringKey("faas.version");

  /** The execution environment ID as a string. */
  public static final AttributeKey<String> FAAS_INSTANCE = stringKey("faas.instance");

  /**
   * The amount of memory available to the serverless function in MiB.
   *
   * <p>Note: It&#39;s recommended to set this attribute since e.g. too little memory can easily
   * stop a Java AWS Lambda function from working correctly. On AWS Lambda, the environment variable
   * `AWS_LAMBDA_FUNCTION_MEMORY_SIZE` provides this information.
   */
  public static final AttributeKey<Long> FAAS_MAX_MEMORY = longKey("faas.max_memory");

  /** Unique host ID. For Cloud, this must be the instance_id assigned by the cloud provider. */
  public static final AttributeKey<String> HOST_ID = stringKey("host.id");

  /**
   * Name of the host. On Unix systems, it may contain what the hostname command returns, or the
   * fully qualified hostname, or another name specified by the user.
   */
  public static final AttributeKey<String> HOST_NAME = stringKey("host.name");

  /** Type of host. For Cloud, this must be the machine type. */
  public static final AttributeKey<String> HOST_TYPE = stringKey("host.type");

  /** The CPU architecture the host system is running on. */
  public static final AttributeKey<String> HOST_ARCH = stringKey("host.arch");

  /** Name of the VM image or OS install the host was instantiated from. */
  public static final AttributeKey<String> HOST_IMAGE_NAME = stringKey("host.image.name");

  /** VM image ID. For Cloud, this value is from the provider. */
  public static final AttributeKey<String> HOST_IMAGE_ID = stringKey("host.image.id");

  /**
   * The version string of the VM image as defined in [Version
   * Attributes](README.md#version-attributes).
   */
  public static final AttributeKey<String> HOST_IMAGE_VERSION = stringKey("host.image.version");

  /** The name of the cluster. */
  public static final AttributeKey<String> K8S_CLUSTER_NAME = stringKey("k8s.cluster.name");

  /** The name of the Node. */
  public static final AttributeKey<String> K8S_NODE_NAME = stringKey("k8s.node.name");

  /** The UID of the Node. */
  public static final AttributeKey<String> K8S_NODE_UID = stringKey("k8s.node.uid");

  /** The name of the namespace that the pod is running in. */
  public static final AttributeKey<String> K8S_NAMESPACE_NAME = stringKey("k8s.namespace.name");

  /** The UID of the Pod. */
  public static final AttributeKey<String> K8S_POD_UID = stringKey("k8s.pod.uid");

  /** The name of the Pod. */
  public static final AttributeKey<String> K8S_POD_NAME = stringKey("k8s.pod.name");

  /** The name of the Container in a Pod template. */
  public static final AttributeKey<String> K8S_CONTAINER_NAME = stringKey("k8s.container.name");

  /** The UID of the ReplicaSet. */
  public static final AttributeKey<String> K8S_REPLICASET_UID = stringKey("k8s.replicaset.uid");

  /** The name of the ReplicaSet. */
  public static final AttributeKey<String> K8S_REPLICASET_NAME = stringKey("k8s.replicaset.name");

  /** The UID of the Deployment. */
  public static final AttributeKey<String> K8S_DEPLOYMENT_UID = stringKey("k8s.deployment.uid");

  /** The name of the Deployment. */
  public static final AttributeKey<String> K8S_DEPLOYMENT_NAME = stringKey("k8s.deployment.name");

  /** The UID of the StatefulSet. */
  public static final AttributeKey<String> K8S_STATEFULSET_UID = stringKey("k8s.statefulset.uid");

  /** The name of the StatefulSet. */
  public static final AttributeKey<String> K8S_STATEFULSET_NAME = stringKey("k8s.statefulset.name");

  /** The UID of the DaemonSet. */
  public static final AttributeKey<String> K8S_DAEMONSET_UID = stringKey("k8s.daemonset.uid");

  /** The name of the DaemonSet. */
  public static final AttributeKey<String> K8S_DAEMONSET_NAME = stringKey("k8s.daemonset.name");

  /** The UID of the Job. */
  public static final AttributeKey<String> K8S_JOB_UID = stringKey("k8s.job.uid");

  /** The name of the Job. */
  public static final AttributeKey<String> K8S_JOB_NAME = stringKey("k8s.job.name");

  /** The UID of the CronJob. */
  public static final AttributeKey<String> K8S_CRONJOB_UID = stringKey("k8s.cronjob.uid");

  /** The name of the CronJob. */
  public static final AttributeKey<String> K8S_CRONJOB_NAME = stringKey("k8s.cronjob.name");

  /** The operating system type. */
  public static final AttributeKey<String> OS_TYPE = stringKey("os.type");

  /**
   * Human readable (not intended to be parsed) OS version information, like e.g. reported by `ver`
   * or `lsb_release -a` commands.
   */
  public static final AttributeKey<String> OS_DESCRIPTION = stringKey("os.description");

  /** Process identifier (PID). */
  public static final AttributeKey<Long> PROCESS_PID = longKey("process.pid");

  /**
   * The name of the process executable. On Linux based systems, can be set to the `Name` in
   * `proc/[pid]/status`. On Windows, can be set to the base name of `GetProcessImageFileNameW`.
   */
  public static final AttributeKey<String> PROCESS_EXECUTABLE_NAME =
      stringKey("process.executable.name");

  /**
   * The full path to the process executable. On Linux based systems, can be set to the target of
   * `proc/[pid]/exe`. On Windows, can be set to the result of `GetProcessImageFileNameW`.
   */
  public static final AttributeKey<String> PROCESS_EXECUTABLE_PATH =
      stringKey("process.executable.path");

  /**
   * The command used to launch the process (i.e. the command name). On Linux based systems, can be
   * set to the zeroth string in `proc/[pid]/cmdline`. On Windows, can be set to the first parameter
   * extracted from `GetCommandLineW`.
   */
  public static final AttributeKey<String> PROCESS_COMMAND = stringKey("process.command");

  /**
   * The full command used to launch the process as a single string representing the full command.
   * On Windows, can be set to the result of `GetCommandLineW`. Do not set this if you have to
   * assemble it just for monitoring; use `process.command_args` instead.
   */
  public static final AttributeKey<String> PROCESS_COMMAND_LINE = stringKey("process.command_line");

  /**
   * All the command arguments (including the command/executable itself) as received by the process.
   * On Linux-based systems (and some other Unixoid systems supporting procfs), can be set according
   * to the list of null-delimited strings extracted from `proc/[pid]/cmdline`. For libc-based
   * executables, this would be the full argv vector passed to `main`.
   */
  public static final AttributeKey<List<String>> PROCESS_COMMAND_ARGS =
      stringArrayKey("process.command_args");

  /** The username of the user that owns the process. */
  public static final AttributeKey<String> PROCESS_OWNER = stringKey("process.owner");

  /**
   * The name of the runtime of this process. For compiled native binaries, this SHOULD be the name
   * of the compiler.
   */
  public static final AttributeKey<String> PROCESS_RUNTIME_NAME = stringKey("process.runtime.name");

  /**
   * The version of the runtime of this process, as returned by the runtime without modification.
   */
  public static final AttributeKey<String> PROCESS_RUNTIME_VERSION =
      stringKey("process.runtime.version");

  /**
   * An additional description about the runtime of the process, for example a specific vendor
   * customization of the runtime environment.
   */
  public static final AttributeKey<String> PROCESS_RUNTIME_DESCRIPTION =
      stringKey("process.runtime.description");

  /**
   * Logical name of the service.
   *
   * <p>Note: MUST be the same for all instances of horizontally scaled services. If the value was
   * not specified, SDKs MUST fallback to `unknown_service:` concatenated with
   * [`process.executable.name`](process.md#process), e.g. `unknown_service:bash`. If
   * `process.executable.name` is not available, the value MUST be set to `unknown_service`.
   */
  public static final AttributeKey<String> SERVICE_NAME = stringKey("service.name");

  /**
   * A namespace for `service.name`.
   *
   * <p>Note: A string value having a meaning that helps to distinguish a group of services, for
   * example the team name that owns a group of services. `service.name` is expected to be unique
   * within the same namespace. If `service.namespace` is not specified in the Resource then
   * `service.name` is expected to be unique for all services that have no explicit namespace
   * defined (so the empty/unspecified namespace is simply one more valid namespace). Zero-length
   * namespace string is assumed equal to unspecified namespace.
   */
  public static final AttributeKey<String> SERVICE_NAMESPACE = stringKey("service.namespace");

  /**
   * The string ID of the service instance.
   *
   * <p>Note: MUST be unique for each instance of the same `service.namespace,service.name` pair (in
   * other words `service.namespace,service.name,service.instance.id` triplet MUST be globally
   * unique). The ID helps to distinguish instances of the same service that exist at the same time
   * (e.g. instances of a horizontally scaled service). It is preferable for the ID to be persistent
   * and stay the same for the lifetime of the service instance, however it is acceptable that the
   * ID is ephemeral and changes during important lifetime events for the service (e.g. service
   * restarts). If the service has no inherent unique ID that can be used as the value of this
   * attribute it is recommended to generate a random Version 1 or Version 4 RFC 4122 UUID (services
   * aiming for reproducible UUIDs may also use Version 5, see RFC 4122 for more recommendations).
   */
  public static final AttributeKey<String> SERVICE_INSTANCE_ID = stringKey("service.instance.id");

  /** The version string of the service API or implementation. */
  public static final AttributeKey<String> SERVICE_VERSION = stringKey("service.version");

  /** The name of the telemetry SDK as defined above. */
  public static final AttributeKey<String> TELEMETRY_SDK_NAME = stringKey("telemetry.sdk.name");

  /** The language of the telemetry SDK. */
  public static final AttributeKey<String> TELEMETRY_SDK_LANGUAGE =
      stringKey("telemetry.sdk.language");

  /** The version string of the telemetry SDK. */
  public static final AttributeKey<String> TELEMETRY_SDK_VERSION =
      stringKey("telemetry.sdk.version");

  /** The version string of the auto instrumentation agent, if used. */
  public static final AttributeKey<String> TELEMETRY_AUTO_VERSION =
      stringKey("telemetry.auto.version");

  /** The name of the web engine. */
  public static final AttributeKey<String> WEBENGINE_NAME = stringKey("webengine.name");

  /** The version of the web engine. */
  public static final AttributeKey<String> WEBENGINE_VERSION = stringKey("webengine.version");

  /** Additional description of the web engine (e.g. detailed version and edition information). */
  public static final AttributeKey<String> WEBENGINE_DESCRIPTION =
      stringKey("webengine.description");

  // Enum definitions
  public static final class CloudProviderValues {
    /** Amazon Web Services. */
    public static final String AWS = "aws";
    /** Microsoft Azure. */
    public static final String AZURE = "azure";
    /** Google Cloud Platform. */
    public static final String GCP = "gcp";

    private CloudProviderValues() {}
  }

  public static final class CloudPlatformValues {
    /** AWS Elastic Compute Cloud. */
    public static final String AWS_EC2 = "aws_ec2";
    /** AWS Elastic Container Service. */
    public static final String AWS_ECS = "aws_ecs";
    /** AWS Elastic Kubernetes Service. */
    public static final String AWS_EKS = "aws_eks";
    /** AWS Lambda. */
    public static final String AWS_LAMBDA = "aws_lambda";
    /** AWS Elastic Beanstalk. */
    public static final String AWS_ELASTIC_BEANSTALK = "aws_elastic_beanstalk";
    /** Azure Virtual Machines. */
    public static final String AZURE_VM = "azure_vm";
    /** Azure Container Instances. */
    public static final String AZURE_CONTAINER_INSTANCES = "azure_container_instances";
    /** Azure Kubernetes Service. */
    public static final String AZURE_AKS = "azure_aks";
    /** Azure Functions. */
    public static final String AZURE_FUNCTIONS = "azure_functions";
    /** Azure App Service. */
    public static final String AZURE_APP_SERVICE = "azure_app_service";
    /** Google Cloud Compute Engine (GCE). */
    public static final String GCP_COMPUTE_ENGINE = "gcp_compute_engine";
    /** Google Cloud Run. */
    public static final String GCP_CLOUD_RUN = "gcp_cloud_run";
    /** Google Cloud Kubernetes Engine (GKE). */
    public static final String GCP_KUBERNETES_ENGINE = "gcp_kubernetes_engine";
    /** Google Cloud Functions (GCF). */
    public static final String GCP_CLOUD_FUNCTIONS = "gcp_cloud_functions";
    /** Google Cloud App Engine (GAE). */
    public static final String GCP_APP_ENGINE = "gcp_app_engine";

    private CloudPlatformValues() {}
  }

  public static final class AwsEcsLaunchtypeValues {
    /** ec2. */
    public static final String EC2 = "ec2";
    /** fargate. */
    public static final String FARGATE = "fargate";

    private AwsEcsLaunchtypeValues() {}
  }

  public static final class HostArchValues {
    /** AMD64. */
    public static final String AMD64 = "amd64";
    /** ARM32. */
    public static final String ARM32 = "arm32";
    /** ARM64. */
    public static final String ARM64 = "arm64";
    /** Itanium. */
    public static final String IA64 = "ia64";
    /** 32-bit PowerPC. */
    public static final String PPC32 = "ppc32";
    /** 64-bit PowerPC. */
    public static final String PPC64 = "ppc64";
    /** 32-bit x86. */
    public static final String X86 = "x86";

    private HostArchValues() {}
  }

  public static final class OsTypeValues {
    /** Microsoft Windows. */
    public static final String WINDOWS = "windows";
    /** Linux. */
    public static final String LINUX = "linux";
    /** Apple Darwin. */
    public static final String DARWIN = "darwin";
    /** FreeBSD. */
    public static final String FREEBSD = "freebsd";
    /** NetBSD. */
    public static final String NETBSD = "netbsd";
    /** OpenBSD. */
    public static final String OPENBSD = "openbsd";
    /** DragonFly BSD. */
    public static final String DRAGONFLYBSD = "dragonflybsd";
    /** HP-UX (Hewlett Packard Unix). */
    public static final String HPUX = "hpux";
    /** AIX (Advanced Interactive eXecutive). */
    public static final String AIX = "aix";
    /** Oracle Solaris. */
    public static final String SOLARIS = "solaris";
    /** IBM z/OS. */
    public static final String Z_OS = "z_os";

    private OsTypeValues() {}
  }

  public static final class TelemetrySdkLanguageValues {
    /** cpp. */
    public static final String CPP = "cpp";
    /** dotnet. */
    public static final String DOTNET = "dotnet";
    /** erlang. */
    public static final String ERLANG = "erlang";
    /** go. */
    public static final String GO = "go";
    /** java. */
    public static final String JAVA = "java";
    /** nodejs. */
    public static final String NODEJS = "nodejs";
    /** php. */
    public static final String PHP = "php";
    /** python. */
    public static final String PYTHON = "python";
    /** ruby. */
    public static final String RUBY = "ruby";
    /** webjs. */
    public static final String WEBJS = "webjs";

    private TelemetrySdkLanguageValues() {}
  }

  private ResourceAttributes() {}
}
