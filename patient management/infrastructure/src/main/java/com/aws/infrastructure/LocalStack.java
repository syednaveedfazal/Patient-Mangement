package com.aws.infrastructure;

import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.rds.*;

public class LocalStack extends Stack {
    private static final Logger log = LoggerFactory.getLogger(LocalStack.class);

    private final Vpc vpc;
    public LocalStack(final App scope , final String id, final StackProps props)
    {
        super(scope,id,props);
        this.vpc = createVpc();
        DatabaseInstance authServiceDb = createDatabase("AuthServiceDb","auth-service-db");
        DatabaseInstance billingServiceDb = createDatabase("PatientServiceDB","billing-service-db");
    }
    private Vpc createVpc()
    {
        return   Vpc.Builder.create(this,"PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2)
                .build();

    }

    private DatabaseInstance createDatabase(String id,String dbName){
        return DatabaseInstance.Builder
                .create(this,id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps
                                .builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()
                ))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                //compute power and size of instance
                .allocatedStorage(20)
                // storage in gigabytes
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    public static void main(final String[]args)
    {

        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        // this will create a infrastructure template in cdk.out file ,
        // sysnthesizer is used to convert type of infrastructure we need
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();
        new LocalStack(app,"LocalStack", props);
        app.synth();
        System.out.println("App Synstesizing in progress");
    }
}
