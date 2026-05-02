# AiHelpMeGetJob

## 🇬🇧 [English](README.md) / 🇺🇸 [中文](README-zh-CN.md)

## 🇺🇸 English Documentation

### Project Overview
This is an Android application based on accessibility services, intelligently analyzes job descriptions and requirements for job seekers, matches positions, and conducts initial communications.

### Core Features
- **Accessibility Parsing**: Read Boss Zhiping app interface using Android AccessibilityService
- **Job Extraction**: Automatically identify and extract key information from job cards
- **AI Analysis**: LLM Agent intelligently analyzes job match scores and conducts preliminary communication
- **Automation**: One-click startup, can quickly communicate with multiple positions


### Usage Steps
   - Open this application, set relevant parameters including job-related information, personal resume information, and LLM API KEY/URL information
   - Start task from home page, the app will detect if accessibility mode is enabled, if not enabled, it will guide to accessibility mode enable interface, only after enabling can start the task
   - After starting the task, automatically start Boss Zhiping application, begin to detect positions and automatic communication


### Important Notes
- ⚠️ Must run in accessibility mode
- ⚠️ Be sure to set all parameters correctly before starting tasks to avoid illegal scenarios
- ⚠️ LLM API provides test button, be sure to fill in and test to ensure API is available
- ⚠️ This application is only for learning and exchange purposes, please do not use for illegal purposes
