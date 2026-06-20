package main

import (
	"context"
	"encoding/json" // <-- REMOVE OR DELETE THIS LINE COMPLETELY
	"net/http"
	"os"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/sqs"
)

type CalculationRequest struct {
	RideID   string  `json:"rideId"`
	BaseFare float64 `json:"baseFare"`
	Distance float64 `json:"distance"`
	Time     float64 `json:"time"`
}

type CalculationResponse struct {
	RideID    string  `json:"rideId"`
	TotalFare float64 `json:"totalFare"`
}

func handler(ctx context.Context, request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {
	var req CalculationRequest
	err := json.Unmarshal([]byte(request.Body), &req)
	if err != nil {
		return events.APIGatewayProxyResponse{StatusCode: http.StatusBadRequest, Body: "Invalid request payload"}, nil
	}

	// Business Logic Calculation
	// Standard Formula: Base + (Distance * 1.5) + (Time * 0.5)
	totalFare := req.BaseFare + (req.Distance * 1.5) + (req.Time * 0.5)

	// Async Logging: Prepare SQS Payload
	cfg, _ := config.LoadDefaultConfig(ctx)
	sqsClient := sqs.NewFromConfig(cfg)
	queueURL := os.Getenv("QUEUE_URL")

	logPayload, _ := json.Marshal(map[string]interface{}{
		"rideId":    req.RideID,
		"totalFare": totalFare,
		"status":    "PROCESSED",
	})

	_, _ = sqsClient.SendMessage(ctx, &sqs.SendMessageInput{
		QueueUrl:    &queueURL,
		MessageBody: ptr(string(logPayload)),
	})

	res := CalculationResponse{RideID: req.RideID, TotalFare: totalFare}
	responseBody, _ := json.Marshal(res)

	return events.APIGatewayProxyResponse{
		StatusCode: http.StatusOK,
		Headers:    map[string]string{"Content-Type": "application/json"},
		Body:       string(responseBody),
	}, nil
}

func ptr(s string) *string { return &s }

func main() {
	lambda.Start(handler)
}
