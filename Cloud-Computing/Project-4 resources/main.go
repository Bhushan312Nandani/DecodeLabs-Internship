package main

import (
	"context"
	"encoding/json"
	"fmt"
	"os"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
)

type AuditLog struct {
	RideID    string  `json:"rideId"`
	TotalFare float64 `json:"totalFare"`
	Status    string  `json:"status"`
}

func handler(ctx context.Context, sqsEvent events.SQSEvent) error {
	cfg, _ := config.LoadDefaultConfig(ctx)
	dbClient := dynamodb.NewFromConfig(cfg)
	tableName := os.Getenv("TABLE_NAME")

	for _, message := range sqsEvent.Records {
		var log AuditLog
		_ = json.Unmarshal([]byte(message.Body), &log)

		_, _ = dbClient.PutItem(ctx, &dynamodb.PutItemInput{
			TableName: &tableName,
			Item: map[string]types.AttributeValue{
				"rideId":    &types.AttributeValueMemberS{Value: log.RideID},
				"totalFare": &types.AttributeValueMemberN{Value: jsonNumber(log.TotalFare)},
				"status":    &types.AttributeValueMemberS{Value: log.Status},
			},
		})
	}
	return nil
}

func jsonNumber(f float64) string { return fmt.Sprintf("%f", f) }

func main() {
	lambda.Start(handler)
}
