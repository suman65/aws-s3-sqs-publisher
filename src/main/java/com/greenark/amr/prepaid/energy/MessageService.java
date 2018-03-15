package com.greenark.amr.prepaid.energy;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.greenark.amr.prepaid.energy.dto.FileUploadBean;

@Service
public class MessageService {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private AmazonS3Client s3Client;

	private static final String SUFFIX = "/";
	private static final String bucketName = UUID.randomUUID() + "-"
			+ DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());
	private static String folderName = "images";

	// @Value("${task.queue.name}")
	private String queueName = "task-queuetask-queuedf06a432-d32b-4afb-83bc-3530633c25f8";;

	public void sendMessage(final String message) {

		// jmsTemplate.convertAndSend(destinationName, message, postProcessor);

		jmsTemplate.send(queueName, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}

	public void uploadImage(FileUploadBean bean) {
		try {
			if (s3Client.doesBucketExist(bucketName)) {
				System.out.format("Bucket %s already exists.\n", bucketName);
			} else {
				try {
					s3Client.createBucket(bucketName);
				} catch (AmazonS3Exception e) {
					System.err.println(e.getErrorMessage());
				}
			}
			for (Bucket bucket : s3Client.listBuckets()) {
				System.out.println(" - " + bucket.getName());
			}

			File uploadFile = new File(bean.getFile().getOriginalFilename());
			uploadFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(uploadFile);
			fos.write(bean.getFile().getBytes());
			fos.close();
			String fileName = folderName + SUFFIX + bean.getFile().getOriginalFilename();
			s3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile));
			URL url = s3Client.getUrl(bucketName, fileName);
			System.out.println(url.toString());
			
			jmsTemplate.send(queueName, new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(url.toString());
				}
			});
			uploadFile.delete();
			deleteBucketAndAllContents(s3Client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteBucketAndAllContents(AmazonS3 client) {

		ObjectListing objectListing = client.listObjects(bucketName);

		while (true) {
			for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext();) {
				S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
				client.deleteObject(bucketName, objectSummary.getKey());
			}

			if (objectListing.isTruncated()) {
				objectListing = client.listNextBatchOfObjects(objectListing);
			} else {
				break;
			}
		}
		;

		VersionListing list = client.listVersions(new ListVersionsRequest().withBucketName(bucketName));

		for (Iterator<?> iterator = list.getVersionSummaries().iterator(); iterator.hasNext();) {
			S3VersionSummary s = (S3VersionSummary) iterator.next();
			client.deleteVersion(bucketName, s.getKey(), s.getVersionId());
		}

		client.deleteBucket(bucketName);

	}

}
